package com.sakurawald.api;

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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sakurawald.debug.LoggerManager;
import com.sakurawald.utils.NetworkUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

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
public class QingYunKe_API {

    private static String getRequestURL(String question) {
        return "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + NetworkUtil.encodeURL(question);
    }

    private static String decodeAnswer(String answer) {
        return answer.replace("{br}", "\n");
    }

    public static String getAnswer(String question) {

        /** 获取JSON数据 **/
        String JSON_Enh = getAnswer_Enh(question);
	// 若未找到结果，则返回null
	JSONObject jo = JSON.parseObject(JSON_Enh);
	JSONObject response = jo;
	String content = response.getString("answer");
	if(response.getIntValue("have_img")==1){
		try {
			LoggerManager.logDebug("local", "URL: "+response.getString("img_url"));
			String imageurl = response.getString("img_url");
			URL imageobj = new URL(imageurl);
			HttpURLConnection connection = (HttpURLConnection) imageobj.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36 Edg/94.0.992.47");
			InputStream responseStream = connection.getInputStream();
			ByteArrayOutputStream copyimage = cloneInputStream(responseStream);
			InputStream giveS = new ByteArrayInputStream(copyimage.toByteArray());
			// Image uploadImage = ExternalResource.uploadAsImage(NetworkUtil.getInputStream(response.getString("img_url")),PluginMain.getCurrentBot().getGroups().stream().findAny().get());
			Image uploadImage = ExternalResource.uploadAsImage(giveS, PluginMain.getCurrentBot().getGroups().stream().findAny().get());
			content = content + "[mirai:image:" + uploadImage.getImageId() + "]";
		} catch (Exception e){
			if (true){
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				content = content + sw.toString();
			}
			LoggerManager.logDebug("Local", "Image Process Error");
		}
	}
        LoggerManager.logDebug("Local", "Get Answer >> " + content);
        return content;
    }

    public static String getAnswer_Enh(String question){
        Connection conn = null;
        PreparedStatement stmt = null;
	boolean exc = false;
	String excs = "";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://${YOUR SQL ADDRESS", "${YOUR SQL USERNAME}", "${YOUR SQL UASE PWD}");
            String sql;
            sql = "select * from miraichat where ? regexp question order by char_length(question) desc";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, question);
            ResultSet res = stmt.executeQuery();
            if (res != null){
		res.next();
		String ans = res.getString("answer");
		boolean have_img = (res.getInt("have_img") != 0);
		String img_url = res.getString("img_URL");
		String ques = res.getString("question");
		JSONObject returnobject = new JSONObject();
		returnobject.put("question", question);
		returnobject.put("match", ques);
		returnobject.put("answer", ans);
		returnobject.put("have_img", have_img);
		returnobject.put("img_url", img_url);
		returnobject.put("exc", false);
		returnobject.put("excs", "");
		return returnobject.toJSONString();
	    }
	} catch(Exception e) {
	    
	}
        String JSON_aaa = getAnswer_JSON(question);
        if (JSON_aaa == null) {
            return "{}";
        }
        JSONObject jo = JSON.parseObject(JSON_aaa);
        JSONObject response = jo;
        String content = response.getString("content");
        content = decodeAnswer(content);
        LoggerManager.logDebug("QingYunKe", "Get Answer >> " + content);
        JSONObject returno2 = new JSONObject();
	returno2.put("question", question);
	returno2.put("match", "api");
	returno2.put("answer", content);
	returno2.put("have_img", false);
	returno2.put("img_url", "");
	returno2.put("exc", exc);
	returno2.put("excs", excs);
	return returno2.toJSONString();
	
    }


    private static String getAnswer_JSON(String question) {

        LoggerManager.logDebug("QingYunKe", "Get Answer -> Run");

        String result = null;

        OkHttpClient client = new OkHttpClient();

        Request request;
        String URL = getRequestURL(question);
        LoggerManager.logDebug("QingYunKe", "Request URL >> " + URL);
        request = new Request.Builder().url(URL).get().build();

        Response response = null;

        String JSON = null;
        try {
            response = client.newCall(request).execute();
            LoggerManager.logDebug("QingYunKe", "Request Response >> " + response);
            JSON = response.body().string();
            result = JSON;
        } catch (IOException e) {
            LoggerManager.logError(e);
        }

        LoggerManager.logDebug("QingYunKe",
                "Get Answer >> Response: JSON = " + JSON);

        /** 关闭Response的body **/
        if (response != null) {
            Objects.requireNonNull(response.body()).close();
        }

        return result;
    }
    private static ByteArrayOutputStream cloneInputStream(InputStream input){
        try{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = input.read(buffer)) > -1){
			baos.write(buffer,0,len);
		}
		baos.flush();
		return baos;
	} catch (IOException e){
		return null;
	}
    }
}
