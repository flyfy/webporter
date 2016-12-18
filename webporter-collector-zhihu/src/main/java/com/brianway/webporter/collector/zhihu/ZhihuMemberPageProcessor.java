package com.brianway.webporter.collector.zhihu;

import com.brianway.webporter.configure.Configuration;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by brian on 16/12/19.
 */
public class ZhihuMemberPageProcessor implements PageProcessor {
    private static String URL_TEMPLATE = "https://www.zhihu.com/api/v4/members/%s";

    private static String QUERY_PARAMS = "?include=locations%2Cemployments%2Cgender%2Ceducations%2Cbusiness%2Cvoteup_count%2Cthanked_Count%2Cfollower_count%2Cfollowing_count%2Ccover_url%2Cfollowing_topic_count%2Cfollowing_question_count%2Cfollowing_favlists_count%2Cfollowing_columns_count%2Canswer_count%2Carticles_count%2Cpins_count%2Cquestion_count%2Cfavorite_count%2Cfavorited_count%2Clogs_count%2Cmarked_answers_count%2Cmarked_answers_text%2Cmessage_thread_token%2Caccount_status%2Cis_active%2Cis_force_renamed%2Cis_bind_sina%2Csina_weibo_url%2Csina_weibo_name%2Cshow_sina_weibo%2Cis_blocking%2Cis_blocked%2Cmutual_followees_count%2Cvote_to_count%2Cvote_from_count%2Cthank_to_count%2Cthank_from_count%2Cthanked_count%2Cdescription%2Chosted_live_count%2Cparticipated_live_count%2Callow_message%2Cindustry_category%2Corg_name%2Corg_homepage%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics";
    private Site site = getConfigured();

    public void process(Page page) {
        page.putField(ZhihuMemberPipeline.URL, page.getUrl());
        page.putField(ZhihuMemberPipeline.RESPONSE, page.getRawText());
    }

    public Site getSite() {
        return site;
    }

    private Site getConfigured() {
        Site site = Configuration.getConfiguredSite();
        site.setRetryTimes(3).setSleepTime(10);
        return site;
    }

    private static String generateMemberUrl(String urlToken) {
        return String.format(URL_TEMPLATE, urlToken) + QUERY_PARAMS;
    }

    public static void main(String[] args) {
        String pipelinePath = "/Users/brian/todo/data/webmagic";
        String tokenPath = "/Users/brian/todo/data/backup/url_tokens/users.txt";
        ZhihuMemberUrlTokenGetter getter = new ZhihuMemberUrlTokenGetter();

        Spider spider = Spider.create(new ZhihuFolloweePageProcessor())
                .setScheduler(new FixedFileCacheQueueScheduler(pipelinePath))
                .addPipeline(new ZhihuMemberPipeline(pipelinePath))
                .thread(20);

        for (String token : getter.getUrlTokens(tokenPath)) {
            spider.addUrl(generateMemberUrl(token));
        }

        spider.run();
    }

}
