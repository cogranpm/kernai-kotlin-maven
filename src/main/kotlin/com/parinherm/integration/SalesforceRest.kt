package com.parinherm.integration

import com.parinherm.audio.speech.NumberWordConverter
import java.io.IOException;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ContextBuilder;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.io.DefaultClassicHttpResponseFactory;
import org.apache.hc.core5.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.hc.core5.http.impl.io.DefaultHttpResponseParser;
import org.apache.hc.core5.http.impl.io.DefaultHttpResponseParserFactory;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.HttpMessageParser;
import org.apache.hc.core5.http.io.HttpMessageParserFactory;
import org.apache.hc.core5.http.io.HttpMessageWriterFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicLineParser;
import org.apache.hc.core5.http.message.LineParser;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.CharArrayBuffer;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;


import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;


/*****************************
 * Note, you need to get a token
 * run these commands in a terminal to get the token
 * sf org login web
 * sf org display --target-org mussym@empathetic-shark-fm41xe.com
 * output has the token, paste it in the SalesforceConfig record in ui
 * need to refresh it every so often
 * have tried to do the auth flows via the generateNewAccessToken method in this object
 * but it doesn't work so far
 */
object SalesforceRest {
    //const val token = "00D8b00000148u1!ARQAQHfidINGk3pJs7fiVhp6fw9nBtfDMF9163..WA_4zdu78kY1nGlqYCSqVyRmE0fF12qlP.aIDB.oyNhEES25u44Atl8J"
    //val LOGINURL = "https://empathetic-shark-fm41xe-dev-ed.my.salesforce.com";

    public fun generateNewAccessToken(baseUrl: String){

        /*
        https://empathetic-shark-fm41xe-dev-ed.my.salesforce.com/services/oauth2/token
        client id: 3MVG99gP.VbJma8X6Lz4ywmLX3frfVOObt0I7BRk59PGCRoxyPJGqqo22UPMv2V3JZAIZpQ8nFjCdf5ZfUURu
        secret:842914540A9A5FADFCC9EC44E617240CFBD62A8CAF7CD7EFD0AA4EF52751937D

        post request
        request body
        grant_type: "client_credentials"
        client_id: "3MVG99gP.VbJma8X6Lz4ywmLX3frfVOObt0I7BRk59PGCRoxyPJGqqo22UPMv2V3JZAIZpQ8nFjCdf5ZfUURu"
        client_secret: "842914540A9A5FADFCC9EC44E617240CFBD62A8CAF7CD7EFD0AA4EF52751937D"

        response body
       {
       "access_token":"00D8b00000148u1!ARQAQNA30FmV70fIswQZg_CjUOVq47_IlUHUBmUCDjMjVaTTnCusCk1kWWO2pcWwTMy77XsmO89C5wKlOPmpfaiM4lyTywJE",
       "signature":"W405z3d+ebrU70p3qxX1A6qKPjOCqvUuRy46f9LMpoA=",
       "instance_url":"https://empathetic-shark-fm41xe-dev-ed.my.salesforce.com",
       "id":"https://login.salesforce.com/id/00D8b00000148u1EAA/0058b00000F9cHjAAJ",
       "token_type":"Bearer",
       "issued_at":"1727278284181"
       }
         */
        val parameters: JSONObject = JSONObject()
        parameters.put("grant_type", "client_credentials")
        //parameters.put("grant_type", "password")
        parameters.put("client_id", "3MVG99gP.VbJma8X6Lz4ywmLX3frfVOObt0I7BRk59PGCRoxyPJGqqo22UPMv2V3JZAIZpQ8nFjCdf5ZfUURu")
        parameters.put("client_secret", "842914540A9A5FADFCC9EC44E617240CFBD62A8CAF7CD7EFD0AA4EF52751937D")
        parameters.put("username", "mussym@empathetic-shark-fm41xe.com")
        parameters.put("password", "Reddingo!23")
        HttpClients.createDefault().use {
            val url = "${baseUrl}/services/oauth2/token"
            val post = HttpPost(url)
            post.addHeader("Content-Type", "application/x-www-form-urlencoded")
            var body: StringEntity = StringEntity(parameters.toString())
            post.entity = body
            val response = it.execute(post)
            val entity = response.entity
            val jsonResponse = EntityUtils.toString(entity)
            println(jsonResponse)
        }
    }

    public fun test(url: String, token: String): String {
        var returnValue = ""
        HttpClients.createDefault().use {
            //val get = HttpGet("https://empathetic-shark-fm41xe-dev-ed.lightning.force.com/services/data/v60.0/sobjects/pse_Project_Task__c/describe/")
            val get = HttpGet(url)
            get.addHeader("Authorization", "Bearer $token")
            val response = it.execute(get)
            val entity = response.entity
            returnValue = EntityUtils.toString(entity)

        }
        return returnValue
    }

    /*
    public fun createLead(url: String,
                          token: String,
                          firstName: String,
                          lastName: String,
                          company: String,
                          mobilePhone: String) {
        val lead: JSONObject = JSONObject()
        lead.put("FirstName", firstName)
        lead.put("LastName", lastName)
        lead.put("Company", company)
        lead.put("MobilePhone", mobilePhone)
        lead.put("Status", "Working - Contacted")

        HttpClients.createDefault().use {
            val post = HttpPost(url)
            post.addHeader("Authorization", "Bearer $token")
            post.addHeader("Content-Type", "application/json")
            var body: StringEntity = StringEntity(lead.toString())
            post.entity = body
            val response = it.execute(post)
            println(response.code)
        }
    }
     */

    private fun createLead(token: String, baseUrl: String, parameters: List<Pair<String, String>>) : Int {
        val lead: JSONObject = JSONObject()
        var result: Int = 0;

        /*
        LeadSource (Web, Phone Inquiry, Partner Referral, Purchased List, Other)
        Email
        MobilePhone
         */
        for(pair in parameters){
           when(pair.first.lowercase().filterNot { it.isWhitespace() }){
               "firstname" -> lead.put("FirstName", pair.second)
               "lastname" -> lead.put("LastName", pair.second)
               "company" -> lead.put("Company", pair.second)
               "mobilephone" -> lead.put("MobilePhone", NumberWordConverter.wordToNumber(pair.second).toString())
               "source" -> lead.put("LeadSource", pair.second)
           }

        }
        lead.put("Status", "Working - Contacted")

        //required fields check
        if(!lead.has("FirstName")){
            return result
        }
        HttpClients.createDefault().use {
            val createLeadUrl = "${baseUrl}/services/data/v32.0/sobjects/Lead/"
            val post = HttpPost(createLeadUrl)
            post.addHeader("Authorization", "Bearer $token")
            post.addHeader("Content-Type", "application/json")
            var body: StringEntity = StringEntity(lead.toString())
            post.entity = body
            val response = it.execute(post)
            result = response.code
        }
        return result
    }

    public fun parseCommand(token: String, baseUrl: String, commandStack: ArrayDeque<String>): Int {
        //first command is the rest command to perform, for example create lead
        val command = commandStack.first()
        //remaining are the arguments
        val parameters = commandStack.drop(1)
        val parsedParameters = parseCommandParameters(parameters)
        var result: Int = 0
        result = when(command.lowercase()){
            "create lead" ->  createLead(token, baseUrl, parsedParameters)
            else -> 0
        }
        return result
    }

    private fun parseCommandParameters(parameters: List<String>) : List<Pair<String, String>>{
        return parameters.map { Pair(it.substringBefore("is").trim(), it.substringAfter("is").trim())}
    }
}