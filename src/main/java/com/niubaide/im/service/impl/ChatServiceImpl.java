package com.niubaide.im.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.niubaide.im.dao.GroupInfoDao;
import com.niubaide.im.model.po.GroupInfo;
import com.niubaide.im.model.vo.ResponseJson;
import com.niubaide.im.service.ChatService;
import com.niubaide.im.util.ChatType;
import com.niubaide.im.util.Constant;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map.Entry;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private GroupInfoDao groupDao;

    @Override
    public void register(JSONObject param, ChannelHandlerContext ctx) {
        String userId = (String) param.get("userId");
        Constant.onlineUserMap.put(userId, ctx);
        String responseJson = new ResponseJson().success()
                .setData("type", ChatType.REGISTER)
                .toString();
        sendMessage(ctx, responseJson);
        log.info(MessageFormat.format("userId为 {0} 的用户登记到在线用户表，当前在线人数为：{1}"
                , userId, Constant.onlineUserMap.size()));
    }

    @Override
    public void singleSend(JSONObject param, ChannelHandlerContext ctx) {
        String fromUserId = (String) param.get("fromUserId");
        String toUserId = (String) param.get("toUserId");
        String content = (String) param.get("content");
        ChannelHandlerContext toUserCtx = Constant.onlineUserMap.get(toUserId);
        if (toUserCtx == null) {
            String responseJson = new ResponseJson()
                    .error(MessageFormat.format("userId为 {0} 的用户没有登录！", toUserId))
                    .toString();
            sendMessage(ctx, responseJson);
        } else {
            String responseJson = new ResponseJson().success()
                    .setData("fromUserId", fromUserId)
                    .setData("content", content)
                    .setData("type", ChatType.SINGLE_SENDING)
                    .toString();
            sendMessage(toUserCtx, responseJson);
        }
    }

    @Override
    public void groupSend(JSONObject param, ChannelHandlerContext ctx) {

        String fromUserId = (String) param.get("fromUserId");
        String toGroupId = (String) param.get("toGroupId");
        String content = (String) param.get("content");
        
        /*String userId = (String)param.get("userId");
        String fromUsername = (String)param.get("fromUsername");*/
        /*String responseJson = new ResponseJson().success()
                .setData("fromUsername", fromUsername)
                .setData("content", content)
                .setData("type", ChatType.GROUP_SENDING)
                .toString();*/
        /*Set<Entry<String, ChannelHandlerContext>> userCtxs = Constant.onlineUserMap.entrySet();
        for (Entry<String, ChannelHandlerContext> userCtx : userCtxs) {
            if (!userCtx.getKey().equals(userId)) {
                sendMessage(userCtx.getValue(), responseJson);
            }
        }*/
        GroupInfo groupInfo = groupDao.getByGroupId(toGroupId);
        if (groupInfo == null) {
            String responseJson = new ResponseJson().error("该群id不存在").toString();
            sendMessage(ctx, responseJson);
        } else {
            String responseJson = new ResponseJson().success()
                    .setData("fromUserId", fromUserId)
                    .setData("content", content)
                    .setData("toGroupId", toGroupId)
                    .setData("type", ChatType.GROUP_SENDING)
                    .toString();
            groupInfo.getMembers().stream()
                    .forEach(member -> {
                        ChannelHandlerContext toCtx = Constant.onlineUserMap.get(member.getUserId());
                        if (toCtx != null && !member.getUserId().equals(fromUserId)) {
                            sendMessage(toCtx, responseJson);
                        }
                    });
        }
    }

    @Override
    public void remove(ChannelHandlerContext ctx) {
        Iterator<Entry<String, ChannelHandlerContext>> iterator =
                Constant.onlineUserMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, ChannelHandlerContext> entry = iterator.next();
            if (entry.getValue() == ctx) {
                log.info("正在移除握手实例...");
                Constant.webSocketHandshakerMap.remove(ctx.channel().id().asLongText());
                log.info(MessageFormat.format("已移除握手实例，当前握手实例总数为：{0}"
                        , Constant.webSocketHandshakerMap.size()));
                iterator.remove();
                log.info(MessageFormat.format("userId为 {0} 的用户已退出聊天，当前在线人数为：{1}"
                        , entry.getKey(), Constant.onlineUserMap.size()));
                break;
            }
        }
    }

    @Override
    public void FileMsgSingleSend(JSONObject param, ChannelHandlerContext ctx) {
        String fromUserId = (String) param.get("fromUserId");
        String toUserId = (String) param.get("toUserId");
        String originalFilename = (String) param.get("originalFilename");
        String fileSize = (String) param.get("fileSize");
        String fileUrl = (String) param.get("fileUrl");
        ChannelHandlerContext toUserCtx = Constant.onlineUserMap.get(toUserId);
        if (toUserCtx == null) {
            String responseJson = new ResponseJson()
                    .error(MessageFormat.format("userId为 {0} 的用户没有登录！", toUserId))
                    .toString();
            sendMessage(ctx, responseJson);
        } else {
            String responseJson = new ResponseJson().success()
                    .setData("fromUserId", fromUserId)
                    .setData("originalFilename", originalFilename)
                    .setData("fileSize", fileSize)
                    .setData("fileUrl", fileUrl)
                    .setData("type", ChatType.FILE_MSG_SINGLE_SENDING)
                    .toString();
            sendMessage(toUserCtx, responseJson);
        }
    }

    @Override
    public void FileMsgGroupSend(JSONObject param, ChannelHandlerContext ctx) {
        String fromUserId = (String) param.get("fromUserId");
        String toGroupId = (String) param.get("toGroupId");
        String originalFilename = (String) param.get("originalFilename");
        String fileSize = (String) param.get("fileSize");
        String fileUrl = (String) param.get("fileUrl");
        GroupInfo groupInfo = groupDao.getByGroupId(toGroupId);
        if (groupInfo == null) {
            String responseJson = new ResponseJson().error("该群id不存在").toString();
            sendMessage(ctx, responseJson);
        } else {
            String responseJson = new ResponseJson().success()
                    .setData("fromUserId", fromUserId)
                    .setData("toGroupId", toGroupId)
                    .setData("originalFilename", originalFilename)
                    .setData("fileSize", fileSize)
                    .setData("fileUrl", fileUrl)
                    .setData("type", ChatType.FILE_MSG_GROUP_SENDING)
                    .toString();
            groupInfo.getMembers().stream()
                    .forEach(member -> {
                        ChannelHandlerContext toCtx = Constant.onlineUserMap.get(member.getUserId());
                        if (toCtx != null && !member.getUserId().equals(fromUserId)) {
                            sendMessage(toCtx, responseJson);
                        }
                    });
        }
    }

    @Override
    public void typeError(ChannelHandlerContext ctx) {
        String responseJson = new ResponseJson()
                .error("该类型不存在！")
                .toString();
        sendMessage(ctx, responseJson);
    }


    private void sendMessage(ChannelHandlerContext ctx, String message) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    }


}
