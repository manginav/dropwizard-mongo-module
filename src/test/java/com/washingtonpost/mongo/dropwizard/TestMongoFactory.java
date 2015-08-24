package com.washingtonpost.mongo.dropwizard;

import com.mongodb.DB;
import com.washingtonpost.mongo.dropwizard.exceptions.NullDBNameException;
import java.net.UnknownHostException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * <p>Tests our MongoFactory can properly assemble mongo URIs and DBs objects</p>
 */
public class TestMongoFactory {

    @Test(expected=IllegalStateException.class)
    public void testDefinesUserWithoutPassword() {
        MongoFactory factory = new MongoFactory();
        factory.setUser("user");
        factory.buildMongoClientURI();
    }

    @Test(expected=IllegalStateException.class)
    public void testDefinesPasswordWithoutUser() {
        MongoFactory factory = new MongoFactory();
        factory.setPass("pass");
        factory.buildMongoClientURI();
    }

    @Test(expected=NullPointerException.class)
    public void testDoesNotDefineHosts() {
        MongoFactory factory = new MongoFactory();
        factory.setUser("user");
        factory.setPass("pass");
        factory.buildMongoClientURI();
    }

    @Test
    public void testMongoClientURIWithOneHost() {
        MongoFactory factory = new MongoFactory();
        factory.setUser("user");
        factory.setPass("pass");
        factory.setHosts("someserver:1234");
        assertThat(factory.buildMongoClientURI().toString())
                .isEqualTo("mongodb://user:pass@someserver:1234");
    }

    @Test
    public void testMongoClientURIWithMultipleHosts() {
        MongoFactory factory = new MongoFactory();
        factory.setUser("user");
        factory.setPass("pass");
        factory.setHosts("someserver:1234,anotherserver:5678");
        assertThat(factory.buildMongoClientURI().toString())
                .isEqualTo("mongodb://user:pass@someserver:1234,anotherserver:5678");
    }

    @Test
    public void testMongoClientURIWithNoCredentialsIsOk() {
        MongoFactory factory = new MongoFactory();
        factory.setHosts("whatever:567");
        assertThat(factory.buildMongoClientURI().toString())
                .isEqualTo("mongodb://whatever:567");
    }

    @Test
    public void testMongoClientURIWithDbNameDefined() {
        MongoFactory factory = new MongoFactory();
        factory.setHosts("whatever:432");
        factory.setDbName("foo");
        assertThat(factory.buildMongoClientURI().toString())
                .isEqualTo("mongodb://whatever:432/foo");
    }

    @Test
    public void testMongoClientURIWithJustOptions() {
        MongoFactory factory = new MongoFactory();
        factory.setHosts("whatever:432");
        factory.setOptions("option1=x");
        assertThat(factory.buildMongoClientURI().toString())
                .isEqualTo("mongodb://whatever:432/?option1=x");
    }

    @Test
    public void testMongoClientURIWithEverything() {
        MongoFactory factory = new MongoFactory();
        factory.setUser("user");
        factory.setPass("pass");
        factory.setHosts("whatever:432,blah:987");
        factory.setDbName("myCollection");
        factory.setOptions("option1=x,option2=y");
        assertThat(factory.buildMongoClientURI().toString())
                .isEqualTo("mongodb://user:pass@whatever:432,blah:987/myCollection?option1=x,option2=y");
    }

    @Test(expected=NullDBNameException.class)
    public void testTryingToBuildDbWithNoDbNameThrowsException() throws UnknownHostException {
        MongoFactory factory = new MongoFactory();
        factory.setHosts("localhost");
        factory.buildDB();
    }

    @Test
    public void testBuildDB() throws UnknownHostException {
        MongoFactory factory = new MongoFactory();
        factory.setHosts("localhost");
        factory.setDbName("foo");
        DB db = factory.buildDB();
        assertThat(db.getName()).isEqualTo("foo");
    }

    @Test
    public void testDisabledModule() throws UnknownHostException {
        MongoFactory factory = new MongoFactory();
        factory.setDisabled(true);

        assertNotNull("Expected a non-null client when disable=true", factory.buildClient());
        assertNotNull("Expected a non-null DB when disabled=true", factory.buildDB());
    }
}
