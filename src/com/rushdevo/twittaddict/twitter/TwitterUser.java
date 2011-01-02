package com.rushdevo.twittaddict.twitter;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitterUser {
	private String screenName;
	private String name;
	private String avatar;
	private String url;
	private Long id;
	private String description;
	private Integer friendsCount;
	private Integer statusesCount;
	
	public TwitterUser(JSONObject userData) {
		try {
			this.screenName = userData.getString("screen_name");
			this.name = userData.getString("name");
			this.avatar = userData.getString("profile_image_url");
			this.url = userData.getString("url");
			this.id = userData.getLong("id");
			this.description = userData.getString("description");
			this.friendsCount = userData.getInt("friends_count");
			this.statusesCount = userData.getInt("statuses_count");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setFriendsCount(Integer friendsCount) {
		this.friendsCount = friendsCount;
	}

	public Integer getFriendsCount() {
		return friendsCount;
	}

	public void setStatusesCount(Integer statusesCount) {
		this.statusesCount = statusesCount;
	}

	public Integer getStatusesCount() {
		return statusesCount;
	}
}
