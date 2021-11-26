package com.jobesk.kikiiapp.Netwrok;

public class Constants {


    /**
     * -------------------- EDIT THIS WITH YOURS -------------------------------------------------
     */


//    public static String WEB_URL ="https://kikii.jobesk.com/api/";
    public static String WEB_URL = "https://kikii.uk/api/";

    public static String INSTAGRAM_ACCESS_TOKEN = "https://api.instagram.com/oauth/";
    public static String INSTAGRAM_FIELDS = "https://graph.instagram.com/";

    public static final String MEMBER_SEARCH_PARAM = "member_search_param";
    public static final String CATEGORY_SEARCH_PARAM = "category_search_param";
    public static final String SUBCATEGORY_SEARCH_PARAM = "subcategory_search_param";
    public static final String LAT_SEARCH_PARAM = "lat_search_param";
    public static final String LONG_SEARCH_PARAM = "long_search_param";

    public static final String SPECIFIC_USER_ID = "specific_user_id";
    public static boolean COMING_FROM_ALERT = false;

    /* [ IMPORTANT ] be careful when edit this security code */
    /* This string must be same with security code at Server, if its different android unable to submit order */
    public static final String SECURITY_CODE = "8V06LupAaMBLtQqyqTxmcN42nn27FlejvaoSM3zXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

    // Device will re-register the device data to server when open app N-times
    public static int OPEN_COUNTER = 50;

    //    values for calling
    public static String SP_NAME = "SP_NAME";
    public static String PREF_USER_ID = "PREF_USER_ID";
    public static final String PA_LABEL_CALL_IN = "PA_LABEL_CALL_IN";

    /**
     * ------------------- DON'T EDIT THIS -------------------------------------------------------
     */

    // this limit value used for give pagination (request and display) to decrease payload
    public static int NEWS_PER_REQUEST = 10;
    public static int PRODUCT_PER_REQUEST = 10;
    public static int NOTIFICATION_PAGE = 20;
    public static int WISHLIST_PAGE = 20;

    // retry load image notification
    public static int LOAD_IMAGE_NOTIF_RETRY = 3;

    // Method get path to image
    public static String getURLimgProduct(String file_name) {
        return WEB_URL + "uploads/product/" + file_name;
    }

    public static String getURLimgNews(String file_name) {
        return WEB_URL + "uploads/news/" + file_name;
    }

    public static String getURLimgCategory(String file_name) {
        return WEB_URL + "uploads/category/" + file_name;
    }

    public static int SPAN_COUNTS = 0;
    public static boolean IS_CHANGED = false;

    public static final String TITLE = "title";
    public static final String CATEGORY_ID = "category_id";
    public static final String EVENT_ID = "event_id";
    public static final String SUBCATEGORY_ID = "subcategory_id";
    public static final String STARTS_AT = "starts_at";
    public static final String ENDS_AT = "ends_at";
    public static final String ATTACHMENT = "attachment";
    public static final String PAID = "paid";
    public static final String LIMITED = "limited";
    public static final String AMOUNT = "amount";
    public static final String TOKEN = "token";
    public static final String TYPE = "type";
    public static final String TICKETS = "tickets";
    public static final String COMMENT = "comment";
    public static final String TAGS = "tags";
    public static final String IDS = "ids";
    public static final String COMMENT_ID = "comment_id";
    public static final String PROFILE_ID = "profile_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PHONE = "phone";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String BIRTHDAY = "birthday";
    public static final String PROFILE_PIC = "profile_pic";
    public static final String SINGLE = "single";
    public static final String MULTIPLE = "multiple";
    public static final String UID = "uid";
    public static final String URL = "url";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "grant_type";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String BODY = "body";
    public static final String OFFSET = "offset";
    public static final String POST_ID = "post_id";
    public static final String ID = "id";
    public static final String NOT_SET = "N/A";
    public static final String BIO = "bio";
    public static final String DISTANCE = "distance";
    public static final String FROM_AGE = "from_age";
    public static final String TO_AGE = "to_age";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String USER_MATCH_ID = "user_match_id";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String INCOGNITO = "incognito";
    public static final String UPGRADED = "upgraded";
    public static final String SHOW_LOCATION = "show_location";
    public static final String MEDIA = "media[]";
    public static final String NEW_MEDIA = "new_media[]";
    public static final String DELETED_MEDIA_IDS = "deleted_media_ids";
    public static final String IMAGE = "image";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String START_NAME = "start_name";
    public static final String CREATE_RIDE_OBJ = "obj";

    /**
     * Categories keyword
     ***/
    public static final String GENDER_IDENTITY = "gender_identity";
    public static final String SEXUAL_IDENTITY = "sexual_identity";
    public static final String PRONOUNS = "pronouns";
    public static final String RELATIONSHIP_STATUS = "relationship_status";
    public static final String HEIGHT = "height";
    public static final String DISTANCE_IN = "distance_in";
    public static final String LOOKING_FOR = "looking_for";
    public static final String DRINK = "drink";
    public static final String DIET_LIKE = "diet_like";
    public static final String SMOKE = "smoke";
    public static final String CANNABIS = "cannabis";
    public static final String POLITICAL_VIEWS = "political_views";
    public static final String RELIGION = "religion";
    public static final String SIGN = "sign";
    public static final String PETS = "pets";
    public static final String KIDS = "kids";
    /***********************/

    public static final String INSTAGRAM_LOGIN_LINK = "https://api.instagram.com/oauth/authorize?client_id=940190363169482&redirect_uri=https://www.instagram.com/&scope=user_profile&response_type=code";

}
