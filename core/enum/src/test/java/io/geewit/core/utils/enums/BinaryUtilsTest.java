package io.geewit.core.utils.enums;

import org.junit.Test;

public class BinaryUtilsTest {

    @Test
    public void toBinaryTest() {
        int orgs = BinaryUtils.toBinary(UserExtraInfoRequest.orgs);
        System.out.println("orgs = " + orgs);

        int currentOrgs = BinaryUtils.toBinary(UserExtraInfoRequest.currentOrgs);
        System.out.println("currentOrgs = " + currentOrgs);

        int orgsAndCompanies = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        System.out.println("orgsAndCompanies = " + orgsAndCompanies);
    }


}
