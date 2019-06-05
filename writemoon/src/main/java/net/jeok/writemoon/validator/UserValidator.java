package net.jeok.writemoon.validator;

import java.util.Set;

import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import net.jeok.writemoon.model.User;
import net.jeok.writemoon.service.UserService;

/**
 * 회원 가입 항목 입력 확인
 */
@Component
public class UserValidator implements Validator {
    
	@Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    /**
     * 아이디 = 필수 항목, 공백과 중복 금지,
     * 비밀번호 = 필수 항목, 공백 금지, 8 <= 비밀번호 길이 <= 32
     */
    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 4 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
        
        //생일은 공백이 아니고 8자리여야한다.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthdate", "NotEmpty");
        if (user.getBirthdate().length() != 8)
        {
        	errors.rejectValue("birthdate", "Size.userForm.birthdate");
        }
        int year = Integer.parseInt(user.getBirthdate().substring(0, 4));
        int month = Integer.parseInt(user.getBirthdate().substring(4,6));
        int day = Integer.parseInt(user.getBirthdate().substring(6,8));
        int[] thedays = {31,28,31,30,31,30,31,31,30,31,30,31};	// 월당 해당하는 일의 개수
        if(year%4==0 && year%100!=0 || year%400==0) // 윤년일시 2월에 하루 더함
        {
        	thedays[1]++;
        }
        if (month < 1 || month > 12) // 생일 월이 적절하게 입력되었는지 검사
        {
        	errors.rejectValue("birthdate", "Size.userForm.bmonth"); 	
        }
        else
        {
        	if (day < 1 || day > thedays[month-1]) //생일 일이 적절하게 입력되었는지 검사
        	{
        		errors.rejectValue("birthdate", "Size.userForm.bday");         	
        	}        	
        }
    }
}