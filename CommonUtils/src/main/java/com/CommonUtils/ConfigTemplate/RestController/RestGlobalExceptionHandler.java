package com.CommonUtils.ConfigTemplate.RestController;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class RestGlobalExceptionHandler 
{
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MultipartException.class})
    public String handleError2(HttpServletRequest request, Throwable ex) 
    {
    	String message = ""; 
    	MultipartException mEx = (MultipartException)ex; 
    	Throwable cause = ex.getCause().getCause();
    	if (cause instanceof SizeLimitExceededException)
    	{
    		SizeLimitExceededException flEx = (SizeLimitExceededException) cause; 
    		float permittedSize = flEx.getPermittedSize() / 1024 / 1024; 
    		message = "single request max size exceeds "+permittedSize+"MB"; 
    	} 
    	else if (cause instanceof FileSizeLimitExceededException)
    	{ 
    		FileSizeLimitExceededException flEx = (FileSizeLimitExceededException)mEx.getCause().getCause(); 
    		float permittedSize = flEx.getPermittedSize() / 1024 / 1024; 
    		message = "single file max size exceeds "+permittedSize+"MB"; 
    	}
    	else { message = "Please contact your administrator: " + ex.getMessage(); } 
    	return message;
    }
}