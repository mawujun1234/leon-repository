package com.mawujun.generator.rules;

public class URLRule extends AbstractRule{
	private String type="'string'";
	
	private String validator="function(rule, value, callback){if(!(/^http[s]?:\\/\\/.*/.test(value))){callback(new Error('URL格式错误'));} else {callback()}}";
	
	
	private String trigger= "['blur','change']";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public String toString() {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		builder.append("type:"+type);
		builder.append(",validator:"+validator);
		if(super.getMessage()!=null) {
			builder.append(",message:'"+super.getMessage()+"'");
		}
		builder.append(",trigger:"+trigger);
		builder.append("}");
		
		return builder.toString();
	}


}
