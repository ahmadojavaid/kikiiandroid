package com.jobesk.kikiiapp.Netwrok;

import com.google.gson.JsonObject;
import com.jobesk.kikiiapp.Callbacks.CallbackAddComment;
import com.jobesk.kikiiapp.Callbacks.CallbackContinueWithPhone;
import com.jobesk.kikiiapp.Callbacks.CallbackFacebookLogin;
import com.jobesk.kikiiapp.Callbacks.CallbackGetCategory;
import com.jobesk.kikiiapp.Callbacks.CallbackGetCategoryChip;
import com.jobesk.kikiiapp.Callbacks.CallbackGetCommunityPosts;
import com.jobesk.kikiiapp.Callbacks.CallbackGetConversationMessages;
import com.jobesk.kikiiapp.Callbacks.CallbackGetOnlineUsers;
import com.jobesk.kikiiapp.Callbacks.CallbackGetEvents;
import com.jobesk.kikiiapp.Callbacks.CallbackGetMyFriends;
import com.jobesk.kikiiapp.Callbacks.CallbackGetFilters;
import com.jobesk.kikiiapp.Callbacks.CallbackGetKikiiPosts;
import com.jobesk.kikiiapp.Callbacks.CallbackGetMatch;
import com.jobesk.kikiiapp.Callbacks.CallbackGetMeetUsers;
import com.jobesk.kikiiapp.Callbacks.CallbackGetPendingRequests;
import com.jobesk.kikiiapp.Callbacks.CallbackGetPostComments;
import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Callbacks.CallbackGetSentRequests;
import com.jobesk.kikiiapp.Callbacks.CallbackInstagramFields;
import com.jobesk.kikiiapp.Callbacks.CallbackInstagramLogin;
import com.jobesk.kikiiapp.Callbacks.CallbackInstagramOAuth;
import com.jobesk.kikiiapp.Callbacks.CallbackSendMessage;
import com.jobesk.kikiiapp.Callbacks.CallbackSinglePost;
import com.jobesk.kikiiapp.Callbacks.CallbackSpecificUserPosts;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.google.gson.JsonElement;
import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Callbacks.CallbackVerifyOTP;
import com.jobesk.kikiiapp.Model.AdsImagesModel.AdsModel;
import com.jobesk.kikiiapp.Model.BlockedUserModel.BlockedUserModel;
import com.jobesk.kikiiapp.Model.ChannelNameModel;
import com.jobesk.kikiiapp.Model.eventGetModel.GetEventModel;
import com.jobesk.kikiiapp.Model.filters.FiltersModel;
import com.jobesk.kikiiapp.Model.introImages.IntroImagesModel;
import com.jobesk.kikiiapp.Model.notificationModel.NotificationModel;
import com.jobesk.kikiiapp.calling.EndCallModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    //* News Info API ---------------------------------------------------- *//*

    @POST("continue/with-phone")
    @FormUrlEncoded
    Call<CallbackContinueWithPhone> continueWithPhone(@FieldMap Map<String, String> params);

    @POST("continue/with-facebook")
    @FormUrlEncoded
    Call<CallbackFacebookLogin> facebookLogin(@FieldMap Map<String, String> params);

    @POST("continue/with-instagram")
    @FormUrlEncoded
    Call<CallbackInstagramLogin> instagramLogin(@FieldMap Map<String, String> params);

    @POST("update/profile")
    @FormUrlEncoded
    Call<CallbackUpdateProfile> register(@Header("Authorization") String auth,
                                         @FieldMap Map<String, String> params);

    @POST("update/profile")
    @FormUrlEncoded
    Call<CallbackUpdateProfile> updateProfile(@Header("Authorization") String auth,
                                              @FieldMap Map<String, String> params);

    @POST("access_token")
    @FormUrlEncoded
    Call<CallbackInstagramOAuth> instagramAccessToken(@FieldMap Map<String, String> params);

    @POST("verify/phone")
    @FormUrlEncoded
    Call<CallbackVerifyOTP> verifyOTP(@Header("Authorization") String auth,
                                      @FieldMap Map<String, String> params);

    @POST("create/post")
    @FormUrlEncoded
    Call<CallbackStatus> createPost(@Header("Authorization") String auth,
                                    @FieldMap Map<String, String> params);

    @Multipart
    @POST("update/profile")
    Call<CallbackUpdateProfile> updateProfilePhoto(
            @Header("Authorization") String auth,
            @Part MultipartBody.Part file);

    @POST("add/comment")
    @FormUrlEncoded
    Call<CallbackAddComment> addComment(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("update/comment/{id}")
    @FormUrlEncoded
    Call<CallbackAddComment> updateComment(
            @Header("Authorization") String auth,
            @Path("id") String id,
            @FieldMap Map<String, String> params);

    @Multipart
    @POST("add/comment")
    Call<CallbackAddComment> addCommentWithImages(@Header("Authorization") String auth,
                                                  @PartMap Map<String, String> text,
                                                  @Part MultipartBody.Part image);

    @POST("attend/event")
    @FormUrlEncoded
    Call<CallbackStatus> attendEvent(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("update/filters")
    @FormUrlEncoded
    Call<CallbackStatus> updateFilters(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("send/message")
    @FormUrlEncoded
    Call<CallbackSendMessage> sendMessage(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("like/user")
    @FormUrlEncoded
    Call<CallbackStatus> likeUser(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("dislike/user")
    @FormUrlEncoded
    Call<CallbackStatus> dislikeUser(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("follow/user")
    @FormUrlEncoded
    Call<CallbackStatus> followUser(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("unfollow/user")
    @FormUrlEncoded
    Call<CallbackStatus> unFollowUser(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("block/user")
    @FormUrlEncoded
    Call<CallbackStatus> blockUser(
            @Header("Authorization") String auth,
            @FieldMap Map<String, String> params);

    @POST("rewind-swipes")
    Call<CallbackStatus> rewindSwipes(
            @Header("Authorization") String auth);

    @Multipart
    @POST("update/profile")
    Call<JsonElement> uploadFile(@Part MultipartBody.Part file, @Header("Authorization") String authorization);

    @Multipart
    @POST("update/profile")
    Call<JsonElement> uploadFile(@Part MultipartBody.Part file);

    @Multipart
    @POST("create/post")
    Call<CallbackStatus> createPostWithMedia(@Header("Authorization") String auth,
                                             @PartMap Map<String, RequestBody> text,
                                             @Part List<MultipartBody.Part> images);

    @Multipart
    @POST("update/post/{id}")
    Call<CallbackStatus> updatePostWithNewMedia(@Header("Authorization") String auth,
                                                @Path("id") String id,
                                                @PartMap Map<String, RequestBody> text,
                                                @Part List<MultipartBody.Part> images);

    @POST("update/post/{id}")
    @FormUrlEncoded
    Call<CallbackStatus> updatePost(@Header("Authorization") String auth,
                                    @Path("id") String id,
                                    @FieldMap Map<String, String> text);

    @Multipart
    @POST("update/profile")
    Call<CallbackUpdateProfile> updateProfileWithImages(@Header("Authorization") String auth,
                                                        @PartMap Map<String, String> text,
                                                        @Part List<MultipartBody.Part> images);

    @Multipart
    @POST("update/profile")
    Call<CallbackUpdateProfile> updateOtherImages(@Header("Authorization") String auth,
                                                  @Part List<MultipartBody.Part> images);

    @FormUrlEncoded
    @POST("update/profile")
    Call<CallbackUpdateProfile> updateFirebaseToken(@Header("Authorization") String auth,
                                                    @FieldMap Map<String, String> params);

    @GET("me")
    Call<CallbackInstagramFields> instagramGetFields(@Query("fields") String fields,
                                                     @Query("access_token") String access_token);

    @GET("resend/phone-verification-code")
    Call<CallbackStatus> resendOTP(@Header("Authorization") String auth);

    @GET("meet")
    Call<CallbackGetMeetUsers> getMeetUsers(@Header("Authorization") String auth);

    @GET("get/filters")
    Call<CallbackGetFilters> getFilters(@Header("Authorization") String auth);

    @GET("available-filters")
    Call<FiltersModel> availableFilters(@Header("Authorization") String auth);

    @GET("likedislike/post/{id}")
    Call<CallbackStatus> likeDislikePost(@Header("Authorization") String auth,
                                         @Path("id") String id);

    @GET("conversation/messages")
    Call<CallbackGetConversationMessages> getConversationMessages1(@Header("Authorization") String auth,
                                                                   @Query("conversation_id") String conversation_id);


    @GET("conversation/messages")
    Call<CallbackGetConversationMessages> getConversationMessages2(@Header("Authorization") String auth,
                                                                   @Query("user_match_id") String conversation_id);

    @GET("post/comments")
    Call<CallbackGetPostComments> getPostComments(@Header("Authorization") String auth,
                                                  @Query("post_id") String id);

    @GET("post/comments")
    Call<CallbackGetPostComments> getSinglePostComments(@Header("Authorization") String auth,
                                                        @Query("post_id") String id,
                                                        @Query("offset") String next_offset);


    @GET("post")
    Call<CallbackSinglePost> getSinglePost(@Header("Authorization") String auth,
                                           @Query("id") String id);

    @GET("community")
    Call<CallbackGetCommunityPosts> getAllPosts(@Header("Authorization") String auth,
                                                @Query("offset") String next_offset);

    @GET("user/posts")
    Call<CallbackSpecificUserPosts> getUserPosts(@Header("Authorization") String auth,
                                                 @Query("offset") String next_offset,
                                                 @Query("user_id") String user_id);

    @GET("online-users")
    Call<CallbackGetOnlineUsers> getOnlineUsers(@Header("Authorization") String auth);

    @GET("events")
    Call<CallbackGetEvents> getEvents(@Header("Authorization") String auth,
                                      @Query("offset") String next_offset);

    @GET("pending/requests")
    Call<CallbackGetPendingRequests> getPendingRequests(@Header("Authorization") String auth,
                                                        @Query("offset") String next_offset);

    @GET("my/friends")
    Call<CallbackGetMyFriends> getMyFriends(@Header("Authorization") String auth,
                                            @Query("offset") String next_offset);

    @GET("user/friends")
    Call<CallbackGetMyFriends> getUserFriends(@Header("Authorization") String auth,
                                              @Query("user_id") String next_offset);

    @GET("sent/requests")
    Call<CallbackGetSentRequests> getSentRequests(@Header("Authorization") String auth,
                                                  @Query("offset") String next_offset);

    @GET("profile")
    Call<CallbackGetProfile> getProfile(@Header("Authorization") String auth,
                                        @Query("user_id") String user_id);

    @GET("event")
    Call<GetEventModel> getSingleEvent(@Header("Authorization") String auth,
                                       @Query("id") String id);

    @GET("get/category/{name}")
    Call<CallbackGetCategory> getCategory(@Header("Authorization") String auth,
                                          @Path("name") String name);

    @GET("get/category/{name}")
    Call<CallbackGetCategoryChip> getCategoryChip(@Header("Authorization") String auth,
                                                  @Path("name") String name);

    @GET("posts")
    Call<CallbackGetKikiiPosts> getKikiiPosts(@Header("Authorization") String auth,
                                              @Query("offset") String next_offset);

    @GET("match")
    Call<CallbackGetMatch> getMatch(@Header("Authorization") String auth);

    @DELETE("delete/comment/{id}")
    Call<CallbackStatus> deleteComment(@Path("id") String id,
                                       @Header("Authorization") String auth);

    @DELETE("delete/conversation/{id}")
    Call<CallbackStatus> deleteConversation(@Path("id") String id,
                                            @Header("Authorization") String auth);

    @DELETE("delete/post/{id}")
    Call<CallbackStatus> deletePost(@Path("id") String id,
                                    @Header("Authorization") String auth);

    @POST("save/report")
    @FormUrlEncoded
    Call<CallbackStatus> reportPost(@Field("post_id") String id,
                                    @Header("Authorization") String auth);

    @POST("save/report")
    @FormUrlEncoded
    Call<CallbackStatus> reportComment(@Field("comment_id") String id,
                                       @Header("Authorization") String auth);

    @POST("save/report")
    @FormUrlEncoded
    Call<CallbackStatus> reportUser(@Field("user_id") String id,
                                    @Header("Authorization") String auth);

    @GET("ad-images")
    Call<AdsModel> getAddImages();

    @GET("intro-images")
    Call<IntroImagesModel> getAllIntroImages();

    @FormUrlEncoded
    @POST("call")
    Call<ChannelNameModel> makeCall(@Header("Authorization") String auth, @Field("type") String type, @Field("user_id") String id);

    @FormUrlEncoded
    @POST("report/problem")
    Call<JsonObject> reportProblem(@Header("Authorization") String auth, @Field("title") String title, @Field("text") String text);


    @POST("update/profile")
    @FormUrlEncoded
    Call<JsonObject> updateProfileUpgrated(@Header("Authorization") String auth, @FieldMap Map<String, String> params);

    @GET("blocked-users")
    Call<BlockedUserModel> getBlockedUsers(@Header("Authorization") String auth);

    @POST("unblock/user")
    @FormUrlEncoded
    Call<JsonObject> unBlockUser(@Header("Authorization") String auth, @Field("id") String id);

    @DELETE("delete/account/{id}")
    Call<JsonObject> deleteMyAccount(@Header("Authorization") String auth, @Path("id") String id);

    @GET("notifications")
    Call<NotificationModel> getNotifications(@Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("missed-call-notification")
    Call<EndCallModel> endCallApi(@Header("Authorization") String auth, @Field("user_id") String id);


}

