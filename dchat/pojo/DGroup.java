package com.dchat.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 第壹才团即时通讯:好友列表分组bean
 * @Author:Chosen
 * @CrateTime:2017年3月8日
 */
public class DGroup implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String groupname;
	private Set<DChatUser> list;

	
	public DGroup(Integer id, String groupname, Set<DChatUser> list) {
		super();
		this.id = id;
		this.groupname = groupname;
		this.list = list;
	}

	public DGroup() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Set<DChatUser> getList() {
		return list;
	}

	public void setList(Set<DChatUser> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "DGroup [id=" + id + ", groupname=" + groupname + ", list=" + list + "]";
	}
}
