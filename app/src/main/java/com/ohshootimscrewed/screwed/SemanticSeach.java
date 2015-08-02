package com.ohshootimscrewed.screwed;

import com.semantics3.api.Products;

import org.json.JSONObject;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * Created by eric on 8/1/15.
 */
public class SemanticSeach {

    /* Set up a client to talk to the Semantics3 API using your Semantics3 API Credentials */

    public static JSONObject getRequest(String productName) throws OAuthExpectationFailedException {
        Products products = new Products(
                "SEM3C47E5F77DF429631B19413218D63D0C0",
                "YzhmZmM0MTU5MGI5NmQxYzk0MGZmOGVjN2QzNzJiYTc"
        );

        products.productsField("search", productName);

        JSONObject results = null;
        try {
            results = products.getProducts();

            results = products.get();

            return results;
        } catch (OAuthMessageSignerException oamse) {
            oamse.printStackTrace();
        } catch (OAuthCommunicationException oace) {
            oace.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return results;

    }

}
