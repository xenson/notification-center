package com.ctrip.car.osd.notificationcenter.email;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.WebProxy;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

/**
 * Created by xiayx on 2019/11/5.
 */
public class EmailService {
    private String serviceUrl;
    private String proxyHost;
    private Integer proxyPort;
    private String mailAccount;
    private String mailPassword;

    protected ExchangeService exchangeService;
    protected static final Integer folderPageSize = 5;
    protected static final Integer mailPageSize = 50;
    protected static final String attchmentsFolder = "EmailScan_downLoadAttachs";

    protected EmailService() {
    }

    public EmailService(String serviceUrl, String mailAccount, String mailPassword, String proxyHost, Integer proxyPort) throws Exception {
        this.serviceUrl = serviceUrl;
        this.mailAccount = mailAccount;
        this.mailPassword = mailPassword;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        exchangeService = getExchangeService();
    }

    /**
     * exchange mail server connect
     *
     * @return
     * @throws Exception
     */
    public ExchangeService getExchangeService() throws Exception {
        exchangeService = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        if (!StringUtils.isBlank(this.proxyHost)) {
            WebProxy proxy = new WebProxy(this.proxyHost, this.proxyPort);
            exchangeService.setWebProxy(proxy);
        }
        ExchangeCredentials credentials = new WebCredentials(this.mailAccount, this.mailPassword);

        exchangeService.setUrl(new URI(this.serviceUrl));
        exchangeService.setCredentials(credentials);
        exchangeService.setTraceEnabled(true);

        return exchangeService;
    }
}
