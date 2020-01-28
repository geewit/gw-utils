package io.geewit.core.utils.enums;

public enum UserExtraInfoRequest {
    /**
     * 是否需要组织树信息
     */
    orgs,

    /**
     * 是否需要所管理公司集合
     */
    companies,

    /**
     * 是否需要资源码
     */
    resources,

    /**
     * 是否需要最近的上一级公司集合
     */
    parentCompanies,

    /**
     * 是否需要所在组织集合
     */
    currentOrgs
}
