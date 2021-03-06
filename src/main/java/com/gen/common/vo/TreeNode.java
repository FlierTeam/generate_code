/**
 * Copyright 2019-2021 覃海林(qinhaisenlin@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gen.common.vo;

import com.jfinal.core.JFinal;

import java.util.Collection;

public class TreeNode {
	private String id;
	private String text;
	private String icon;
	private String url;
	private Integer levelNo;// 是否菜单
	private String FontIcon;// 字体图标
	private Collection<TreeNode> children;
	private String pid;
	private Boolean spread;//是否展开
	private Integer isWindowOpen;//在window页打开菜单

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public String getHref() {
		if (url != null) {
			if (url.startsWith("http://") || url.startsWith("https://")) {
				return url;
			} else {
				return JFinal.me().getContextPath() + url;
			}
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Collection<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(Collection<TreeNode> children) {
		this.children = children;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Integer getLevelNo() {
		return levelNo;
	}

	public String getFontIcon() {
		return FontIcon;
	}

	public void setLevelNo(Integer levelNo) {
		this.levelNo = levelNo;
	}

	public void setFontIcon(String fontIcon) {
		FontIcon = fontIcon;
	}

	public Boolean getSpread() {
		return spread;
	}

	public void setSpread(Boolean spread) {
		this.spread = spread;
	}

	public Integer getIsWindowOpen() {
		return isWindowOpen;
	}

	public void setIsWindowOpen(Integer isWindowOpen) {
		this.isWindowOpen = isWindowOpen;
	}

}
