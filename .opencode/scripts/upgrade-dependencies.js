#!/usr/bin/env node
/**
 * Query Maven Central for newer release versions and optionally update
 * gradle.properties. This script is adapted from the robot OpenCode helper
 * and trimmed for gw-utils.
 */

const fs = require('fs/promises');
const path = require('path');
const readline = require('readline');

const PROJECT_ROOT = path.join(__dirname, '..', '..');
const DRY_RUN = process.argv.includes('--dry-run');
const AUTO_YES = process.argv.includes('--yes');
const INCLUDE_PRERELEASE = process.argv.includes('--include-prerelease');

const EXTRA_DEPENDENCIES = new Map([
    ['asm.version', { groupId: 'org.ow2.asm', artifactId: 'asm' }],
    ['checkstyle.version', { groupId: 'com.puppycrawl.tools', artifactId: 'checkstyle' }],
    ['jacoco.version', { groupId: 'org.jacoco', artifactId: 'org.jacoco.agent' }],
    ['pmd.version', { groupId: 'net.sourceforge.pmd', artifactId: 'pmd-core' }]
]);

const PRERELEASE_PATTERNS = [
    /-RC\d*$/i,
    /-M\d+(\.\d+)*$/i,
    /-alpha[\d.]*$/i,
    /-beta[\d.]*$/i,
    /-ea[\d+-]*$/i,
    /-SNAPSHOT$/i,
    /-preview[\d.]*$/i
];

const CLASSIFIER_SUFFIX_PATTERNS = [
    /-jdk\d+$/i,
    /-jre\d*$/i,
    /-android\d*$/i
];

function isPrerelease(version) {
    return PRERELEASE_PATTERNS.some((pattern) => pattern.test(version));
}

function hasClassifierSuffix(version) {
    return CLASSIFIER_SUFFIX_PATTERNS.some((pattern) => pattern.test(version));
}

function stripClassifierSuffix(version) {
    for (const pattern of CLASSIFIER_SUFFIX_PATTERNS) {
        const match = version.match(pattern);
        if (match) {
            return version.substring(0, match.index);
        }
    }
    return version;
}

function escapeRegExp(value) {
    return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

async function readProjectFile(filename) {
    return fs.readFile(path.join(PROJECT_ROOT, filename), 'utf8');
}

async function writeProjectFile(filename, content) {
    return fs.writeFile(path.join(PROJECT_ROOT, filename), content, 'utf8');
}

function parseVersionAliases(settingsContent) {
    const aliases = new Map();
    const versionRegex = /(?:catalog\.)?version\s*\(\s*['"]([^'"]+)['"]\s*,\s*(?:getProperty|prop)\s*\(\s*['"]([^'"]+)['"]\s*\)/g;
    let match;
    while ((match = versionRegex.exec(settingsContent)) !== null) {
        aliases.set(match[1], match[2]);
    }
    return aliases;
}

function parseDependencyMappings(settingsContent) {
    const versionAliasToProperty = parseVersionAliases(settingsContent);
    const propertyToMaven = new Map();
    const libraryRegex = /(?:catalog\.)?library\s*\(\s*['"]([^'"]+)['"]\s*,\s*['"]([^'"]+)['"]\s*,\s*['"]([^'"]+)['"]\s*\)\s*\.\s*versionRef\s*\(\s*['"]([^'"]+)['"]\s*\)/g;
    let match;

    while ((match = libraryRegex.exec(settingsContent)) !== null) {
        const [, , groupId, artifactId, versionAlias] = match;
        const propertyKey = versionAliasToProperty.get(versionAlias);
        if (!propertyKey) {
            continue;
        }
        const existing = propertyToMaven.get(propertyKey);
        if (!existing || artifactId.length < existing.artifactId.length) {
            propertyToMaven.set(propertyKey, { groupId, artifactId });
        }
    }

    return propertyToMaven;
}

function resolveArtifactId(groupId, requestedName) {
    if (requestedName) {
        return requestedName;
    }

    const specialCases = {
        'biz.aQute.bnd': 'biz.aQute.bnd.annotation',
        'blue.strategic.parquet': 'parquet-floor',
        'ch.qos.logback': 'logback-classic',
        'co.nstant.in': 'cbor',
        'com.fasterxml': 'classmate',
        'com.fasterxml.woodstox': 'woodstox-core',
        'com.github.luben': 'zstd-jni',
        'com.github.spotbugs': 'spotbugs-annotations',
        'com.google.code.findbugs': 'jsr305',
        'com.google.code.gson': 'gson',
        'com.google.errorprone': 'error_prone_annotations',
        'com.google.j2objc': 'j2objc-annotations',
        'com.jayway.jsonpath': 'json-path',
        'com.nimbusds': 'nimbus-jose-jwt',
        'com.squareup.okhttp3': 'okhttp',
        'com.squareup.okio': 'okio',
        'commons-codec': 'commons-codec',
        'commons-io': 'commons-io',
        'commons-logging': 'commons-logging',
        'de.rototor.pdfbox': 'graphics2d',
        'io.micrometer': 'micrometer-observation',
        'io.netty': 'netty-common',
        'io.projectreactor': 'reactor-core',
        'io.projectreactor.addons': 'reactor-pool',
        'io.projectreactor.netty': 'reactor-netty-core',
        'io.swagger.core.v3': 'swagger-core-jakarta',
        'it.unimi.dsi': 'fastutil',
        'jakarta.activation': 'jakarta.activation-api',
        'jakarta.annotation': 'jakarta.annotation-api',
        'jakarta.validation': 'jakarta.validation-api',
        'jakarta.xml.bind': 'jakarta.xml.bind-api',
        'net.bytebuddy': 'byte-buddy',
        'net.java.dev.jna': 'jna',
        'net.minidev': 'json-smart',
        'org.antlr': 'antlr4-runtime',
        'org.apache.logging.log4j': 'log4j-api',
        'org.apache.lucene': 'lucene-core',
        'org.apache.maven': 'maven-core',
        'org.apache.pdfbox': 'pdfbox',
        'org.apache.tomcat.embed': 'tomcat-embed-core',
        'org.assertj': 'assertj-core',
        'org.awaitility': 'awaitility',
        'org.codehaus.plexus': 'plexus-utils',
        'org.codehaus.woodstox': 'stax2-api',
        'org.hibernate.validator': 'hibernate-validator',
        'org.jbibtex': 'jbibtex',
        'org.jboss.logging': 'jboss-logging',
        'org.jetbrains': 'annotations',
        'org.jetbrains.kotlin': 'kotlin-stdlib',
        'org.jline': 'jline',
        'org.jsoup': 'jsoup',
        'org.objenesis': 'objenesis',
        'org.osgi': 'org.osgi.resource',
        'org.springframework': 'spring-context',
        'org.springframework.ai': 'spring-ai-commons',
        'org.springframework.boot': 'spring-boot',
        'org.springframework.data': 'spring-data-commons',
        'org.springframework.retry': 'spring-retry',
        'org.springframework.security': 'spring-security-core',
        'org.tomlj': 'tomlj',
        'org.tukaani': 'xz',
        'org.xerial.snappy': 'snappy-java',
        'org.xmlunit': 'xmlunit-core',
        'tools.jackson': 'jackson-core'
    };

    return specialCases[groupId] || groupId.split('.').pop();
}

function parseResolutionStrategyMappings(content) {
    const lines = content.split('\n');
    const groups = [];
    const names = [];
    const uses = [];

    for (let i = 0; i < lines.length; i++) {
        const trimmed = lines[i].trim();
        const groupMatch = trimmed.match(/requestedGroup\s*==\s*['"]([^'"]+)['"]|['"]([^'"]+)['"]\s*==\s*requestedGroup|requestedGroup\.startsWith\s*\(\s*['"]([^'"]+)['"]\s*\)/);
        if (groupMatch) {
            let groupId = groupMatch[1] || groupMatch[2] || groupMatch[3];
            if (groupId.endsWith('.')) {
                groupId = groupId.slice(0, -1);
            }
            groups.push({ line: i, groupId });
        }

        const nameMatch = trimmed.match(/requestedName\s*==\s*['"]([^'"]+)['"]/);
        if (nameMatch) {
            names.push({ line: i, name: nameMatch[1] });
        }

        const useMatch = trimmed.match(/useCatalogVersion\s*\(\s*((?:['"][^'"]+['"]\s*,?\s*)+)\)/);
        if (useMatch) {
            const aliases = [];
            const aliasRegex = /['"]([^'"]+)['"]/g;
            let match;
            while ((match = aliasRegex.exec(useMatch[1])) !== null) {
                aliases.push(match[1]);
            }
            uses.push({ line: i, aliases });
        }
    }

    const mappings = new Map();
    for (const use of uses) {
        const group = [...groups].reverse().find((entry) => entry.line < use.line);
        if (!group) {
            continue;
        }
        const name = [...names].reverse().find((entry) => entry.line < use.line && entry.line >= group.line);
        for (const alias of use.aliases) {
            if (!mappings.has(alias)) {
                mappings.set(alias, {
                    groupId: group.groupId,
                    artifactId: resolveArtifactId(group.groupId, name?.name),
                    aliases: use.aliases
                });
            }
        }
    }
    return mappings;
}

function parseProperties(content) {
    const props = new Map();
    for (const line of content.split('\n')) {
        const trimmed = line.trim();
        if (!trimmed || trimmed.startsWith('#')) {
            continue;
        }
        const eq = trimmed.indexOf('=');
        if (eq > 0) {
            props.set(trimmed.substring(0, eq).trim(), trimmed.substring(eq + 1).trim());
        }
    }
    return props;
}

async function queryMavenVersions(groupId, artifactId) {
    const metadataPath = `${groupId.replace(/\./g, '/')}/${artifactId}/maven-metadata.xml`;
    const url = `https://repo.maven.apache.org/maven2/${metadataPath}`;
    const response = await fetch(url, { redirect: 'follow' });

    if (!response.ok) {
        return { success: false, error: response.status === 404 ? 'not found' : `HTTP ${response.status}` };
    }

    const xml = await response.text();
    const releaseVersion = xml.match(/<release>([^<]+)<\/release>/)?.[1] || null;
    const versions = [...xml.matchAll(/<version>([^<]+)<\/version>/g)].map((match) => match[1]);
    return { success: true, releaseVersion, versions };
}

function selectLatestVersion(releaseVersion, versions) {
    if (INCLUDE_PRERELEASE) {
        const candidate = releaseVersion || versions[versions.length - 1];
        return candidate ? { version: candidate, isPrerelease: isPrerelease(candidate) } : null;
    }

    if (releaseVersion && !isPrerelease(releaseVersion)) {
        if (hasClassifierSuffix(releaseVersion)) {
            const baseVersion = stripClassifierSuffix(releaseVersion);
            if (versions.includes(baseVersion) && !isPrerelease(baseVersion)) {
                return { version: baseVersion, isPrerelease: false };
            }
        }
        return { version: releaseVersion, isPrerelease: false };
    }

    for (let i = versions.length - 1; i >= 0; i--) {
        if (!isPrerelease(versions[i]) && !hasClassifierSuffix(versions[i])) {
            return { version: versions[i], isPrerelease: false };
        }
    }
    return null;
}

function isNewer(current, latest) {
    const normalizedCurrent = stripClassifierSuffix(current);
    const normalizedLatest = stripClassifierSuffix(latest);
    if (normalizedCurrent === normalizedLatest) {
        return false;
    }

    const parse = (value) => value.split(/[.-]/).map((part) => {
        const number = Number.parseInt(part, 10);
        return Number.isNaN(number) ? part : number;
    });

    const currentParts = parse(normalizedCurrent);
    const latestParts = parse(normalizedLatest);
    for (let i = 0; i < Math.max(currentParts.length, latestParts.length); i++) {
        const left = currentParts[i] ?? 0;
        const right = latestParts[i] ?? 0;
        if (typeof left === 'number' && typeof right === 'number') {
            if (right > left) return true;
            if (right < left) return false;
        } else {
            if (String(right) > String(left)) return true;
            if (String(right) < String(left)) return false;
        }
    }
    return false;
}

async function askToContinue() {
    const rl = readline.createInterface({ input: process.stdin, output: process.stdout });
    const answer = await new Promise((resolve) => {
        rl.question('Update gradle.properties? (y/N) ', resolve);
    });
    rl.close();
    return answer.trim().toLowerCase() === 'y';
}

async function main() {
    console.log('Parsing dependency configuration...');

    const settingsContent = await readProjectFile('buildSrc/common-settings.gradle');
    const propertyToMaven = parseDependencyMappings(settingsContent);
    const versionAliasToProperty = parseVersionAliases(settingsContent);

    try {
        const resolutionContent = await readProjectFile('buildSrc/common-resolution-strategy.gradle');
        const resolutionMappings = parseResolutionStrategyMappings(resolutionContent);
        for (const [, { groupId, artifactId, aliases }] of resolutionMappings) {
            const propertyKey = aliases
                .map((alias) => versionAliasToProperty.get(alias) || versionAliasToProperty.get(alias.replace(/-/g, '.')))
                .find(Boolean);
            if (propertyKey && !propertyToMaven.has(propertyKey)) {
                propertyToMaven.set(propertyKey, { groupId, artifactId });
            }
        }
    } catch (error) {
        console.warn(`Warning: unable to parse common-resolution-strategy.gradle: ${error.message}`);
    }

    for (const [propertyKey, mavenCoords] of EXTRA_DEPENDENCIES) {
        if (!propertyToMaven.has(propertyKey)) {
            propertyToMaven.set(propertyKey, mavenCoords);
        }
    }

    const propertiesContent = await readProjectFile('gradle.properties');
    const properties = parseProperties(propertiesContent);
    const updates = [];
    const errors = [];
    const skipped = [];
    const entries = [...propertyToMaven.entries()].sort(([left], [right]) => left.localeCompare(right));

    console.log(`Found ${entries.length} version properties with Maven coordinates.`);
    if (!INCLUDE_PRERELEASE) {
        console.log('Prerelease versions are excluded. Use --include-prerelease to include them.');
    }

    for (let i = 0; i < entries.length; i++) {
        const [propertyKey, { groupId, artifactId }] = entries[i];
        const currentVersion = properties.get(propertyKey);
        const prefix = `[${String(i + 1).padStart(2, '0')}/${entries.length}]`;

        if (!currentVersion) {
            skipped.push({ propertyKey, reason: 'missing in gradle.properties' });
            console.log(`${prefix} ${propertyKey}: skipped (missing in gradle.properties)`);
            continue;
        }

        process.stdout.write(`${prefix} ${propertyKey}: ${currentVersion} -> `);
        try {
            const result = await queryMavenVersions(groupId, artifactId);
            if (!result.success) {
                errors.push({ propertyKey, groupId, artifactId, error: result.error });
                console.log(`query failed (${result.error})`);
                continue;
            }

            const selected = selectLatestVersion(result.releaseVersion, result.versions);
            if (!selected) {
                skipped.push({ propertyKey, reason: 'no release version found' });
                console.log('skipped (no release version found)');
                continue;
            }

            if (isNewer(currentVersion, selected.version)) {
                updates.push({
                    propertyKey,
                    oldVersion: currentVersion,
                    newVersion: selected.version,
                    groupId,
                    artifactId,
                    isPrerelease: selected.isPrerelease
                });
                console.log(`${selected.version}${selected.isPrerelease ? ' [prerelease]' : ''}`);
            } else {
                console.log(`${selected.version} (no update)`);
            }
        } catch (error) {
            errors.push({ propertyKey, groupId, artifactId, error: error.message });
            console.log(`query failed (${error.message})`);
        }

        if (i < entries.length - 1) {
            await new Promise((resolve) => setTimeout(resolve, 150));
        }
    }

    console.log('');
    console.log(`Updates: ${updates.length}`);
    console.log(`Errors: ${errors.length}`);
    console.log(`Skipped: ${skipped.length}`);

    if (updates.length === 0) {
        return;
    }

    const maxKeyLength = Math.max(...updates.map((update) => update.propertyKey.length));
    for (const update of updates) {
        const pad = ' '.repeat(maxKeyLength - update.propertyKey.length);
        console.log(`${update.propertyKey}${pad}  ${update.oldVersion} -> ${update.newVersion} (${update.groupId}:${update.artifactId})`);
    }

    if (DRY_RUN) {
        console.log('Dry run: gradle.properties was not modified.');
        return;
    }

    if (!AUTO_YES && !(await askToContinue())) {
        console.log('Cancelled.');
        return;
    }

    let updatedContent = propertiesContent;
    for (const update of updates) {
        const regex = new RegExp(`^(${escapeRegExp(update.propertyKey)}=).*?$`, 'gm');
        updatedContent = updatedContent.replace(regex, `$1${update.newVersion}`);
    }

    await writeProjectFile('gradle.properties', updatedContent);
    console.log(`Updated ${updates.length} version properties in gradle.properties.`);
}

main().catch((error) => {
    console.error(`Error: ${error.message}`);
    process.exit(1);
});
