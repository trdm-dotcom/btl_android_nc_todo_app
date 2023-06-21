package com.example.todo.servies;

import com.example.todo.common.exception.InvalidValueException;
import com.example.todo.config.AppConfig;
import com.example.todo.config.FmConfiguration;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Service
@Slf4j
public class SendEmailService {
    private AppConfig appConf;
    private ResourceLoader resourceLoader;
    private Configuration freeMarkerConfiguration;
    private final String dir;
    private static final String ENCODING = "UTF-8";

    @Autowired
    public SendEmailService(ResourceLoader resourceLoader, AppConfig appConf, FmConfiguration fmConfiguration) {
        this.appConf = appConf;
        this.resourceLoader = resourceLoader;
        this.freeMarkerConfiguration = fmConfiguration.configuration();
        this.dir = appConf.getTemplate().getDir();
    }

    public void sendMail(String subject, List<String> toList, String templateName, Object data, String localeString) throws Exception {
        int type = 0;
        String templateTl = getTemplate(templateName, data, localeString);
        if (templateTl != null) {
            type++;
        } else {
            log.warn("NOT EXIST TL TEMPLATE OF " + templateName);
        }
        String templateHtml = getTemplate(templateName + ".html", data, localeString);
        if (templateHtml != null) {
            type += 2;
        } else {
            log.warn("NOT EXIST HTML TEMPLATE OF " + templateName);
        }
        if (type == 0) {
            throw new InvalidValueException("content");
        }
        JavaMailSenderImpl javaMailSender = getMailSender();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, type == 3, ENCODING);
        helper.setTo(toList.toArray(new String[toList.size()]));
        helper.setSubject(subject);
        helper.setFrom(appConf.getMail().getSender());
        switch (type) {
            case 1:
                helper.setText(templateTl, false);
                break;
            case 2:
                helper.setText(templateHtml, true);
                break;
            case 3:
                helper.setText(templateTl, templateHtml);
                break;
        }
        javaMailSender.send(helper.getMimeMessage());
    }

    public String getTemplate(String templateName, Object data, String localeString) {
        Locale locale = StringUtils.hasText(localeString) ? Locale.forLanguageTag(localeString) : Locale.forLanguageTag("vi");
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        Writer bos = new OutputStreamWriter(bs, StandardCharsets.UTF_8);
        if (!templateName.endsWith(".html") && !templateName.endsWith(".ftl")) {
            templateName += ".ftl";
        }
        try {
            this.freeMarkerConfiguration.setDefaultEncoding(ENCODING);
            this.freeMarkerConfiguration.setEncoding(locale, ENCODING);
            Template template = this.freeMarkerConfiguration.getTemplate(dir + templateName, locale, ENCODING);
            Environment environment = template.createProcessingEnvironment(data, bos);
            environment.setOutputEncoding(ENCODING);
            environment.process();
        } catch (Exception e) {
            log.error("problem on template {} locale {}", templateName, locale, e);
            return null;
        }
        log.info("name {}, locale: {}, result: {}", templateName, localeString, bs);
        return bs.toString();
    }

    private JavaMailSenderImpl getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(appConf.getMail().getEndpoint());
        mailSender.setPort(appConf.getMail().getPort());
        mailSender.setUsername(appConf.getMail().getSmtpUsername());
        mailSender.setPassword(appConf.getMail().getSmtpPassword());

        Properties prop = mailSender.getJavaMailProperties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.trust", appConf.getMail().getEndpoint());
        return mailSender;
    }
}
