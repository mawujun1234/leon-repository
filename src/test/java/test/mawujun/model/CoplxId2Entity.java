package test.mawujun.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
* @author mawujun 16064988
* @createDate ：2018年12月4日 上午11:45:19
*/
@Entity
@Table(name="t_coplxid2entity")
@IdClass(CoplxId2.class)
public class CoplxId2Entity  implements Serializable{
	@Id
	private String id1;
	@Id
	private String id2;
	
	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId1() {
		return id1;
	}

	public void setId1(String id1) {
		this.id1 = id1;
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}
	


}
