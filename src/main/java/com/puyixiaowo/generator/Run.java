package com.puyixiaowo.generator;


/**
 * @author feihong
 * @date 2017-08-13
 */
public class Run {
    public static void main(String[] args) {
        String dbhost = "fblog.db";
        String tables= "user,role,user_role,permission,role_permission" +
                ",menu,article,category,tag,article_tag,afu,afu_type,book,bookshelf" +
                ",book_read,access_record,book_read_setting";
        String src = "src/main/java";
        String domainPackage = "com.puyixiaowo.fblog.domain";

        DomainGenerator.generateDomains(dbhost, tables, src, domainPackage);
    }
}
