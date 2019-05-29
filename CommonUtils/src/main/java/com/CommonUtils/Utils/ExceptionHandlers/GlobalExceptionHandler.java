package com.CommonUtils.Utils.ExceptionHandlers;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//@ControllerAdvice
public class GlobalExceptionHandler 
{
	//http://jira.spring.io/browse/SPR-14651
    //4.3.5 supports RedirectAttributes redirectAttributes
    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) 
    {
        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "redirect:/uploadStatus";
    }
    
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MultipartException.class})
    @ResponseBody
    public Object handleError2(HttpServletRequest request, Throwable ex) 
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