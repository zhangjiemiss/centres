package org.origin.centres.security.client;

import org.origin.centres.utils.AliveUtil;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * @author zhangjie
 * @version 2018-10-25
 * @apiNote Web Client Details Service 配置
 */
public class WebClientDetailsService extends JdbcClientDetailsService {

    private static final String CLIENT_NAME = "sys_client";

    public WebClientDetailsService(DataSource dataSource) {
        super(dataSource);
        this.setDeleteClientDetailsSql(this.getNewSql("deleteClientDetailsSql"));
        this.setFindClientDetailsSql(this.getNewSql("findClientDetailsSql"));
        this.setUpdateClientDetailsSql(this.getNewSql("updateClientDetailsSql"));
        this.setUpdateClientSecretSql(this.getNewSql("updateClientSecretSql"));
        this.setInsertClientDetailsSql(this.getNewSql("insertClientDetailsSql"));
        this.setSelectClientDetailsSql(this.getNewSql("selectClientDetailsSql") + " AND `status` = 0 ");
    }

    /**
     * 获取新sql
     *
     * @param fieldName 字段名
     * @return 新sql
     */
    private String getNewSql(String fieldName) {
        String fieldValue = AliveUtil.getFieldValue(this, fieldName);
        if (fieldValue != null) {
            fieldValue = fieldValue.replaceAll("oauth_client_details", CLIENT_NAME);
        }
        return fieldValue;
    }
}
