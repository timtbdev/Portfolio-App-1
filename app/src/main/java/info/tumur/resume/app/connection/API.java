package info.tumur.resume.app.connection;

import info.tumur.resume.app.connection.callbacks.CallbackCheckVersion;
import info.tumur.resume.app.connection.callbacks.CallbackContact;
import info.tumur.resume.app.connection.callbacks.CallbackPortfolio;
import info.tumur.resume.app.connection.callbacks.CallbackPortfolioItem;
import info.tumur.resume.app.connection.callbacks.CallbackProfile;
import info.tumur.resume.app.connection.callbacks.CallbackTimeline;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: Tumur";

    /* Web Service API transactions */

    // Connection test API and check application version
    @Headers({CACHE, AGENT})
    @GET("services/checkVersion")
    Call<CallbackCheckVersion> getCheckVersion(
            @Query("version") int version // current version code of the app
    );

    // Profile data API
    @Headers({CACHE, AGENT})
    @GET("services/profile")
    Call<CallbackProfile> getProfile();

    // Timeline data API
    @Headers({CACHE, AGENT})
    @GET("services/timeline")
    Call<CallbackTimeline> getTimeline(
            @Query("id") int id
    );

    // Contact data API
    @Headers({CACHE, AGENT})
    @GET("services/contact")
    Call<CallbackContact> getContact();

    // Portfolio data API
    @Headers({CACHE, AGENT})
    @GET("services/portfolio")
    Call<CallbackPortfolio> getPortfolio();

    // Single Portfolio Item data API
    @Headers({CACHE, AGENT})
    @GET("services/portfolioItem")
    Call<CallbackPortfolioItem> getPortfolioItem(
            @Query("id") int id
    );

}
