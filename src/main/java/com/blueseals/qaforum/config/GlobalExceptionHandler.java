package com.blueseals.qaforum.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, Model model) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("exception", ex);
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", ex.getMessage());
        mav.setViewName("error");
        return mav;
    }
}
