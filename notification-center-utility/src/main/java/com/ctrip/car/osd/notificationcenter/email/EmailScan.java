package com.ctrip.car.osd.notificationcenter.email;

import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.*;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiayx on 2019/10/29.
 */
public class EmailScan extends EmailService {

    public EmailScan(String serviceUrl, String mailAccout, String mailPassword, String proxyHost, Integer proxyPort) throws Exception {
        super(serviceUrl, mailAccout, mailPassword, proxyHost, proxyPort);
    }

    /**
     * search specify folder unique id in WellKnownFolder(default in Inbox)
     * specify folder must be in any WellKnownFolder
     * default Inbox if no specify
     *
     * @param specifyFolder
     * @param parentFolder
     * @return
     */
    public Folder searchFolder(String specifyFolder, WellKnownFolderName parentFolder) throws Exception {
        Folder folderObj = null;
        if (StringUtils.isBlank(specifyFolder)) {
            Folder inboxFolder = Folder.bind(exchangeService, WellKnownFolderName.Inbox);
            folderObj = inboxFolder;
            return folderObj;
        }
        if (parentFolder == null) {
            parentFolder = WellKnownFolderName.Inbox;
        }

        FindFoldersResults findFoldersResults = exchangeService.findFolders(parentFolder, new FolderView(folderPageSize));
        for (Folder folder : findFoldersResults.getFolders()) {
            if (specifyFolder.equals(folder.getDisplayName())) {
                folderObj = folder;
                break;
            }
        }
        return folderObj;
    }

    /**
     * search specify folder unique id in WellKnownFolder(default in Inbox)
     * specify folder must be in any WellKnownFolder
     * default Inbox if no specify
     *
     * @param specifyFolder
     * @param parentFolder
     * @return
     */
    public FolderId searchFolderId(String specifyFolder, WellKnownFolderName parentFolder) throws Exception {
        FolderId folderId = null;
        Folder folder = searchFolder(specifyFolder, parentFolder);
        if (folder != null) {
            folderId = folder.getId();
        }
        return folderId;
    }

    /**
     * search mail list from specify account
     * //邮件筛选-多线程,异步--是否已读,搜索量,文件夹,收件时间,文件名称/内容匹配
     *
     * @param specifyFolder
     * @param parentFolder
     * @param isUnread
     * @param pageSize
     * @param subjectReg
     * @param contentReg
     * @return
     * @throws Exception
     */
    public List<EmailMessage> searchMail(String specifyFolder, WellKnownFolderName parentFolder, Boolean isUnread, Integer pageSize, String subjectReg, String contentReg) throws Exception {
        List<EmailMessage> mails = new ArrayList<>();

        Folder aimFolder = searchFolder(specifyFolder, parentFolder);
        if (aimFolder == null) {
            return mails;
        }
        if (pageSize == null) {
            pageSize = mailPageSize;
        }

        // Bind to the folder
        ItemView view = new ItemView(pageSize);
        FindItemsResults<Item> findResults = exchangeService.findItems(aimFolder.getId(), view);
        //MOST IMPORTANT: load messages' properties before
        exchangeService.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties);

        //搜索条件：文件夹ID为SpecailFolderId（我自己指定的收件夹ID）,为大于startDate 这个发件时间的 filter todo
        //FindItemsResults<Item> findResults = exchangeService.findItems(new FolderId(SpecailFolderId), new SearchFilter.IsGreaterThan(ItemSchema.DateTimeSent, startDate), view);

        for (Item item : findResults.getItems()) {
            EmailMessage mailItem = EmailMessage.bind(exchangeService, item.getId());
            // Do something with the item as shown
            if (isUnread != null && !isUnread.equals(!mailItem.getIsRead())) {
                continue;
            }
            mails.add(mailItem);
        }

        return mails;
    }

    /**
     * download attachments to specify path - use for respond attachments and mail
     * //邮件附件处理
     *
     * @param attachs
     * @param path
     * @param folderName
     * @return
     * @throws Exception
     */
    public List<String> downLoadAttachs(List<Attachment> attachs, String path, String folderName) throws Exception {
        List<String> filePaths = new ArrayList<>();
        String folderPath = path + "\\" + attchmentsFolder + "\\" + folderName;
        File file = new File(folderPath);
        file.mkdirs();

        for (Attachment attach : attachs) {
            if (attach instanceof FileAttachment) {
                String filePath = folderPath + "\\" + attach.getName();
                FileOutputStream outputFile = new FileOutputStream(filePath);
                ((FileAttachment) attach).load(outputFile);
                outputFile.close();

                filePaths.add(filePath);
            }
        }
        return filePaths;
    }


    /**
     * move mail to trash
     *
     * @param mail
     * @throws Exception
     */
    public void deleteMail(EmailMessage mail) throws Exception {
        mail.delete(DeleteMode.MoveToDeletedItems);
    }

//    /**
//     * delete specify download attachments from attachments folder
//     *
//     * @param path
//     * @param folderName
//     * @param fileName
//     * @throws Exception
//     */
//    public void deleteAttachFiles(String path, String folderName, String fileName) throws Exception {
//        String folderPath = path + "\\" + attchmentsFolder + "\\" + folderName;
//        File folder = new File(folderPath);
//        if (folder != null && folder.isDirectory() && folder.exists()) {
//            File[] files = folder.listFiles();
//
//            if (files != null && files.length > 0) {
//                for (File file : files) {
//                    if (file.isFile() && file.getName().equals(fileName)) {
//                        System.out.println("file==========" + file.getName());
//                        file.delete();
//                    }
//                }
//            }
//            //delete empty folder
//            if (folder.listFiles() != null && folder.listFiles().length == 0) {
//                folder.delete();
//            }
//        }
//    }

    /**
     * get pure text content from mail body
     *
     * @param html
     * @return
     */
    public String getMailTextContent(String html) {
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;
        try {
            // define script regx <script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            // define style regx <style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            // define HTML target regx
            String regEx_html = "<[^>]+>";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(html);
            // repplace script target
            html = m_script.replaceAll("");
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(html);
            // repplace style target
            html = m_style.replaceAll("");
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(html);
            // repplace html target
            html = m_html.replaceAll("");
            textStr = html;
        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        //replace whitespace
        textStr = textStr.replaceAll("[ ]+", " ");
        textStr = textStr.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
        //unescape from html
        textStr = StringEscapeUtils.unescapeHtml4(textStr);

        return textStr;
    }
}
