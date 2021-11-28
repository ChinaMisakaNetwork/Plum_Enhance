package com.sakurawald.function;

import com.sakurawald.PluginMain;
import com.sakurawald.api.HitoKoto_API;
import com.sakurawald.api.ThirdPartyRandomImage_API;
import com.sakurawald.debug.LoggerManager;
import com.sakurawald.files.FileManager;
import com.sakurawald.framework.MessageManager;
import com.sakurawald.utils.NetworkUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.NudgeEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.event.events.GroupMessageEvent;
public class NudgeFunction extends FunctionManager {

    private static final NudgeFunction instance = new NudgeFunction();

    public static NudgeFunction getInstance() {
        return instance;
    }

    public static void handleEvent(GroupMessageEvent event) {

        if (!FileManager.applicationConfig_File.getSpecificDataInstance().Functions.NudgeFunction.enable) {
            return;
        }
        long fromQQ = event.getSender().getId();
	long fromGroup = event.getGroup().getId();
	String receiveMsg = event.getMessage().contentToString();
	//String[] stringsss = receiveMsg.split(" ", 3);
        // Has "*言" Bot ?
	if(fromGroup==217809220)MessageManager.sendMessageBySituation(fromGroup, fromQQ, receiveMsg);
        if (receiveMsg.equals("坂言")||receiveMsg.equals("一言")) {
            /** 调用间隔合法检测. **/
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
                // Do nothing.
            }

            MessageManager.sendMessageBySituation(fromGroup, fromQQ, sendMsg);
        }

    }

    @Override
    public boolean canUse(long QQGroup) {
        int interval = FileManager.applicationConfig_File.getSpecificDataInstance().Functions.NudgeFunction.perUseIntervalSecond;

        if (this.getFunctionUseHistoryManager().isCallSuccessIntervalLegal(
                QQGroup, interval)) {
            return true;
        }

        return false;
    }
}
