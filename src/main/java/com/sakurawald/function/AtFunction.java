package com.sakurawald.function;
import java.io.*;
import com.sakurawald.PluginMain;
import com.sakurawald.api.HitoKoto_API;
import com.sakurawald.api.ThirdPartyRandomImage_API;
import com.sakurawald.debug.LoggerManager;
import com.sakurawald.files.FileManager;
import com.sakurawald.framework.MessageManager;
import com.sakurawald.utils.NetworkUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.NudgeEvent;
import javax.imageio.*;
import java.lang.Runtime;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import com.sakurawald.PluginMain;
import com.sakurawald.api.QingYunKe_API;
import com.sakurawald.files.FileManager;
import com.sakurawald.framework.MessageManager;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import java.io.File;
import java.io.FileOutputStream;
import java.net.*;
public class AtFunction {

    public static void handleEvent(GroupMessageEvent event) {

        if (!FileManager.applicationConfig_File.getSpecificDataInstance().Functions.AtFunction.enable) {
            return;
        }

        // Has Vaild Call?
        if (event.getMessage().contains(new At(PluginMain.getCurrentBot().getId()))) {
            long fromGroup = event.getGroup().getId();
            long fromQQ = event.getSender().getId();
            String receiveMsg = event.getMessage().contentToString();
            String[] stringsss = receiveMsg.split(" ", 2);
            // Get Answer And SendMsg(From SQL or API).
            String sendMsg = QingYunKe_API.getAnswer(stringsss[1]);
            MessageManager.sendMessageBySituation(fromGroup, fromQQ, sendMsg);
        }
	String receiveMsg1 = event.getMessage().contentToString();
	String[] stringsss1 = receiveMsg1.split(" ", 2);
	long fromGroup = event.getGroup().getId();
	long fromQQ = event.getSender().getId();
	if (stringsss1[0].equals("${THE WORD YOU WANT SET}")){
		String sendMsg = QingYunKe_API.getAnswer(stringsss1[1]);
		MessageManager.sendMessageBySituation(fromGroup, fromQQ, sendMsg);
	}
	if(stringsss1[0].equals("${THE WORD 1}")||stringsss1[0].equals("${THE WORD 2}")){
		if (!NudgeFunction.getInstance().canUse(fromGroup)) {
			MessageManager.sendMessageBySituation(fromGroup, fromQQ, FileManager.applicationConfig_File.getSpecificDataInstance().Functions.FunctionManager.callTooOftenMsg);
			LoggerManager.logDebug("NudgeFunction", "Call too often. Cancel!", true);
			return;
		}
		NudgeFunction.getInstance().updateUseTime(fromGroup);
		String sendMsg = HitoKoto_API.getRandomSentence().getFormatedString();
		try {
			 // Add RandomImage.
			String randomImageURL = ThirdPartyRandomImage_API.getInstance().getRandomImageURL();
			Image uploadImage = ExternalResource.uploadAsImage(NetworkUtil.getInputStream(randomImageURL),
					PluginMain.getCurrentBot().getGroups().stream().findAny().get());
			sendMsg = sendMsg + "\n" + "[mirai:image:" + uploadImage.getImageId() + "]";
		} catch (Exception e) {
		}
		MessageManager.sendMessageBySituation(fromGroup, fromQQ, sendMsg);

	}
    }
}
