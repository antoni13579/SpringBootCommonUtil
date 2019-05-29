package com.CommonUtils.Utils.HttpUtils.Bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RegisterInfo 
{
	private String registerName;
	private String registerPassword;
	private String registerEmail;
}