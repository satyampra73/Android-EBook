package com.example.androidebookapp.rest;

import com.example.androidebookapp.response.AboutUsRP;
import com.example.androidebookapp.response.AppRP;
import com.example.androidebookapp.response.AuthorDetailRP;
import com.example.androidebookapp.response.AuthorRP;
import com.example.androidebookapp.response.AuthorSpinnerRP;
import com.example.androidebookapp.response.BookDetailRP;
import com.example.androidebookapp.response.BookRP;
import com.example.androidebookapp.response.CatRP;
import com.example.androidebookapp.response.CatSpinnerRP;
import com.example.androidebookapp.response.CommentRP;
import com.example.androidebookapp.response.ContactRP;
import com.example.androidebookapp.response.DataRP;
import com.example.androidebookapp.response.DeleteCommentRP;
import com.example.androidebookapp.response.FaqRP;
import com.example.androidebookapp.response.FavouriteRP;
import com.example.androidebookapp.response.GetReportRP;
import com.example.androidebookapp.response.HomeRP;
import com.example.androidebookapp.response.LoginRP;
import com.example.androidebookapp.response.MyRatingRP;
import com.example.androidebookapp.response.PrivacyPolicyRP;
import com.example.androidebookapp.response.ProfileRP;
import com.example.androidebookapp.response.RatingRP;
import com.example.androidebookapp.response.RegisterRP;
import com.example.androidebookapp.response.SubCatRP;
import com.example.androidebookapp.response.SubCatSpinnerRP;
import com.example.androidebookapp.response.TermsConditionsRP;
import com.example.androidebookapp.response.UserCommentRP;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //get app data
    @POST("api.php")
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //login
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //login check
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLoginDetail(@Field("data") String data);

    //register
    @POST("api.php")
    @FormUrlEncoded
    Call<RegisterRP> getRegisterDetail(@Field("data") String data);

    //forget password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> getForgetPassword(@Field("data") String data);

    //profile
    @POST("api.php")
    @FormUrlEncoded
    Call<ProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST("api.php")
    @Multipart
    Call<DataRP> getEditProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //home page
    @POST("api.php")
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    //category
    @POST("api.php")
    @FormUrlEncoded
    Call<CatRP> getCategory(@Field("data") String data);

    //sub category
    @POST("api.php")
    @FormUrlEncoded
    Call<SubCatRP> getSubCategory(@Field("data") String data);

    //category spinner list
    @POST("api.php")
    @FormUrlEncoded
    Call<CatSpinnerRP> getCatSpinner(@Field("data") String data);

    //sub category spinner list
    @POST("api.php")
    @FormUrlEncoded
    Call<SubCatSpinnerRP> getSubCatSpinner(@Field("data") String data);

    //category by id book list
    @POST("api.php")
    @FormUrlEncoded
    Call<BookRP> getCatBook(@Field("data") String data);

    //author
    @POST("api.php")
    @FormUrlEncoded
    Call<AuthorRP> getAuthor(@Field("data") String data);

    //author spinner list
    @POST("api.php")
    @FormUrlEncoded
    Call<AuthorSpinnerRP> getAuthorSpinner(@Field("data") String data);

    //author detail
    @POST("api.php")
    @FormUrlEncoded
    Call<AuthorDetailRP> getAuthorDetail(@Field("data") String data);

    //author by book
    @POST("api.php")
    @FormUrlEncoded
    Call<BookRP> getAuthorBook(@Field("data") String data);

    //continue reading book
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitContinueReading(@Field("data") String data);

    //latest and popular book
    @POST("api.php")
    @FormUrlEncoded
    Call<BookRP> getLatestBook(@Field("data") String data);

    //Favourite book
    @POST("api.php")
    @FormUrlEncoded
    Call<FavouriteRP> getFavouriteBook(@Field("data") String data);

    //related book
    @POST("api.php")
    @FormUrlEncoded
    Call<BookRP> getRelated(@Field("data") String data);

    //search book
    @POST("api.php")
    @FormUrlEncoded
    Call<BookRP> getSearchBook(@Field("data") String data);

    //book detail
    @POST("api.php")
    @FormUrlEncoded
    Call<BookDetailRP> getBookDetail(@Field("data") String data);

    //get all comment
    @POST("api.php")
    @FormUrlEncoded
    Call<CommentRP> getAllComment(@Field("data") String data);

    //comment
    @POST("api.php")
    @FormUrlEncoded
    Call<UserCommentRP> submitComment(@Field("data") String data);

    //delete comment
    @POST("api.php")
    @FormUrlEncoded
    Call<DeleteCommentRP> deleteComment(@Field("data") String data);

    //get my rating
    @POST("api.php")
    @FormUrlEncoded
    Call<MyRatingRP> getMyRating(@Field("data") String data);

    //rating book
    @POST("api.php")
    @FormUrlEncoded
    Call<RatingRP> submitRating(@Field("data") String data);

    //get report book
    @POST("api.php")
    @FormUrlEncoded
    Call<GetReportRP> getBookReport(@Field("data") String data);

    //report book
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitBookReport(@Field("data") String data);

    //get about us
    @POST("api.php")
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST("api.php")
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get terms condition
    @POST("api.php")
    @FormUrlEncoded
    Call<TermsConditionsRP> getTermsCondition(@Field("data") String data);

    //get faq
    @POST("api.php")
    @FormUrlEncoded
    Call<FaqRP> getFaq(@Field("data") String data);


    //get contact us list
    @POST("api.php")
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //Submit contact
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

}
