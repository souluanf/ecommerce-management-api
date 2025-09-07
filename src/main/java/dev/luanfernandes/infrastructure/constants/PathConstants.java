package dev.luanfernandes.infrastructure.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathConstants {

    private static final String API = "/api/v1";

    public static final String AUTH = API + "/auth";
    public static final String AUTH_REGISTER = AUTH + "/register";
    public static final String AUTH_LOGIN = AUTH + "/login";
    public static final String AUTH_REFRESH = AUTH + "/refresh";
    public static final String AUTH_LOGOUT = AUTH + "/logout";

    public static final String PRODUCTS = API + "/products";
    public static final String PRODUCT_ID = PRODUCTS + "/{id}";
    public static final String PRODUCTS_SEARCH = PRODUCTS + "/search";
    public static final String PRODUCTS_SEARCH_SIMPLE = PRODUCTS_SEARCH + "/simple";
    public static final String PRODUCTS_SEARCH_CATEGORY = PRODUCTS_SEARCH + "/category/{category}";
    public static final String PRODUCTS_SEARCH_PRICE_RANGE = PRODUCTS_SEARCH + "/price-range";
    public static final String PRODUCTS_SEARCH_SUGGEST = PRODUCTS_SEARCH + "/suggest";
    public static final String PRODUCTS_SEARCH_ALL = PRODUCTS_SEARCH + "/all";
    public static final String PRODUCTS_SEARCH_ADVANCED = PRODUCTS_SEARCH + "/advanced";
    public static final String PRODUCTS_SEARCH_REINDEX = PRODUCTS_SEARCH + "/reindex";

    public static final String ORDERS = API + "/orders";
    public static final String ORDER_ID = ORDERS + "/{id}";
    public static final String ORDER_PAY = ORDERS + "/{orderId}/pay";

    public static final String REPORTS = API + "/reports";
    public static final String REPORTS_TOP_USERS = REPORTS + "/top-users";
    public static final String REPORTS_USER_TICKETS = REPORTS + "/user-tickets";
    public static final String REPORTS_MONTHLY_REVENUE = REPORTS + "/monthly-revenue";
}
