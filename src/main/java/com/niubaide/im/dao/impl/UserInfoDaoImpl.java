package com.niubaide.im.dao.impl;

import com.niubaide.im.dao.UserInfoDao;
import com.niubaide.im.model.po.GroupInfo;
import com.niubaide.im.model.po.UserInfo;
import com.niubaide.im.util.Constant;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

@Repository
public class UserInfoDaoImpl implements UserInfoDao {

    /**
     * 这里使用死数据，不使用数据库
     */
    @Override
    public void loadUserInfo() {
        // 设置用户基本信息，共9个用户
        List<UserInfo> userInfos = generateUserInfo(9);
        userInfos.forEach(userInfo -> {
            // 设置用户好友列表
            userInfo.setFriendList(generateFriendList(userInfo.getUserId()));

            // 设置用户群列表，共1个群
            GroupInfo groupInfo = new GroupInfo("01", "Group01", "img/avatar/Group01.jpg", null);
            List<GroupInfo> groupList = new ArrayList<>();
            groupList.add(groupInfo);
            userInfo.setGroupList(groupList);

            // 將用戶添加到用戶表
            Constant.userInfoMap.put(userInfo.getUserId(), userInfo);
        });
    }

    @Override
    public UserInfo getByUsername(String username) {
        return Constant.userInfoMap.get(username);
    }

    @Override
    public UserInfo getByUserId(String userId) {
        UserInfo result = null;
        Iterator<Entry<String, UserInfo>> iterator = Constant.userInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, UserInfo> entry = iterator.next();
            if (entry.getValue().getUserId().equals(userId)) {
                result = entry.getValue();
                break;
            }
        }
        return result;
    }

    private List<UserInfo> generateFriendList(String userId) {
        List<UserInfo> friendList = generateUserInfo(10);
        Iterator<UserInfo> iterator = friendList.iterator();
        while (iterator.hasNext()) {
            UserInfo entry = iterator.next();
            if (userId.equals(entry.getUserId())) {
                iterator.remove();
            }
        }
        return friendList;
    }

    private List<UserInfo> generateUserInfo(int num) {
        List<UserInfo> friendList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            String userPre = "{0}";
            String userNamePre = "Member{0}";
            String passPre = "{0}";
            String avaUrlPre = "img/avatar/Member{0}.jpg";
            UserInfo userInfo = new UserInfo(
                    format(userPre, i),
                    format(userNamePre, i),
                    format(passPre, i),
                    format(avaUrlPre, i));
            friendList.add(userInfo);
        }
        return friendList;
    }

    private String format(String source, int target) {
        DecimalFormat complement = new DecimalFormat("000");
        return MessageFormat.format(source, complement.format(target));
    }


}
