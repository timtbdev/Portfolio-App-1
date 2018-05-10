package info.tumur.resume.app.connection;

import info.tumur.resume.app.connection.callbacks.CallbackCheck;
import info.tumur.resume.app.connection.callbacks.CallbackContact;
import info.tumur.resume.app.connection.callbacks.CallbackPortfolio;
import info.tumur.resume.app.connection.callbacks.CallbackPortfolioDetails;
import info.tumur.resume.app.connection.callbacks.CallbackSummaryHeader;
import info.tumur.resume.app.connection.callbacks.CallbackSummarySkills;
import info.tumur.resume.app.connection.callbacks.CallbackSummarySocial;
import info.tumur.resume.app.connection.callbacks.CallbackTimeline;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: Tumur";

    /* Recipe API transaction ------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/check")
    Call<CallbackCheck> getCheck();

    @Headers({CACHE, AGENT})
    @GET("services/summaryHeader")
    Call<CallbackSummaryHeader> getSummaryProile();

    @Headers({CACHE, AGENT})
    @GET("services/summarySocial")
    Call<CallbackSummarySocial> getSummarySocial();

    @Headers({CACHE, AGENT})
    @GET("services/summarySkills")
    Call<CallbackSummarySkills> getSummarySkills();

    @Headers({CACHE, AGENT})
    @GET("services/listTimeline")
    Call<CallbackTimeline> getTimeline(
            @Query("id") int id
    );

    @Headers({CACHE, AGENT})
    @GET("services/contact")
    Call<CallbackContact> getContact();

    @Headers({CACHE, AGENT})
    @GET("services/portfolio")
    Call<CallbackPortfolio> getPortfolio();

    @Headers({CACHE, AGENT})
    @GET("services/portfolioDetails")
    Call<CallbackPortfolioDetails> getPortfolioDetails(
            @Query("id") int id
    );

}
