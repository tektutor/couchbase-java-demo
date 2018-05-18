package org.tektutor;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;

public class Main {

        public static void main ( String args[] ) {

                Cluster cluster = CouchbaseCluster.create("172.17.0.3");
                cluster.authenticate( "Administrator", "admin123" );

                Bucket bucket = cluster.openBucket ( "crm_bucket" );

                JsonObject details = JsonObject.create()
                        .put("duration", "4 days")
                        .put("start_date", "18th May 2018")
                        .put("end_date", "21st May 2018")
                        .put("location", "Chennai");

                JsonArray shipping_addresses = JsonArray.create();
                JsonObject addr1 = JsonObject.create()
                        .put("doorNo", "100")
                        .put("street", "street1")
                        .put("city", "city1");
                JsonObject addr2 = JsonObject.create()
                        .put("doorNo", "200")
                        .put("street", "street2")
                        .put("city", "city2");

                shipping_addresses.add ( addr1 );
                shipping_addresses.add ( addr2 );

                JsonObject python = JsonObject.create()
                        .put( "training", "Python" )
                        .put( "details", details )
                        .put( "shipping_addresses", shipping_addresses );

                bucket.upsert ( JsonDocument.create ( "7", python ) );

                bucket.query ( N1qlQuery.simple( "CREATE PRIMARY INDEX ON crm_bucket" ) );
                bucket.query ( N1qlQuery.simple( "CREATE INDEX crm_secondary_idx ON crm_bucket(`name`, `age`)" ) );

                N1qlQueryResult result = bucket.query ( N1qlQuery.simple( "SELECT shipping_addresses[1].street FROM crm_bucket where training='Python' and shipping_addresses[1].street='street2'" ) );
                
                for ( N1qlQueryRow row : result ) 
                        System.out.println (row);

        
                result = bucket.query ( N1qlQuery.simple( "DELETE FROM crm_bucket WHERE age=10" ) );
                //result = bucket.query ( N1qlQuery.simple( "DROP INDEX crm_bucket.crm_secondary_idx" ) );

                bucket.close();
                
                cluster.disconnect();

                System.exit(0);
	}

}

