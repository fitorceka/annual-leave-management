package com.lhind.annualleavemanagement.util;

public class Constants {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";

    public static final String AUTHENTICATED_USER_IS_NOT_A_MANAGER = "Authenticated user is not a Manager";
    public static final String AUTHENTICATED_USER_IS_NOT_AN_EMPLOYEE = "Authenticated user is not an Employee";
    public static final String AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST = "Authenticated User cannot create an Leave Request";
    public static final String AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST_PROBATION_PERIOD = "You have not yet passed you probation period";

    public static final String TO_DATE_CANNOT_BE_SET_BEFORE_FROM_DATE = "To Date cannot be set before From Date";
    public static final String FROM_DATE_CANNOT_BE_SET_BEFORE_CURRENT_DATE = "From Date cannot be set before Current Date";
    public static final String HIRE_DATE_CANNOT_BE_SET_AFTER_CURRENT_DATE = "Hire Date cannot be set after current day";

    public static final String USER_CANNOT_BE_FOUND_BY_ID = "User not found by ID: userId";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_MANAGER = "User is not an Manager";

    public static final String LEAVE_CANNOT_BE_FOUND_BY_ID = "Leave not found by ID: leaveId";
    public static final String LEAVE_MORE_THAN_20_DAYS = "You do not have more than 20 days per year of leaves";

    public static final String OLD_PASSWORD_DOES_NOT_MATCH = "Old password does not match";

    public static final String MANAGER_REJECTED_LEAVE = "Your manager: %s rejected your leave request";
    public static final String MANAGER_ACCEPTED_LEAVE = "Your manager: %s accepted your leave request";

    public static final String USER_CREATED_LEAVE = "Employee: %s created new leave request.";
    public static final String USER_UPDATED_LEAVE = "Employee: %s %s updated a leave request";
}
