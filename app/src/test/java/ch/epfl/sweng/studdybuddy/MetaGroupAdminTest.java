package ch.epfl.sweng.studdybuddy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.Pair;
import ch.epfl.sweng.studdybuddy.firebase.FirebaseReference;
import ch.epfl.sweng.studdybuddy.firebase.MetaGroup;

import static ch.epfl.sweng.studdybuddy.MetaFactory.deepFBReference;
import static ch.epfl.sweng.studdybuddy.util.CoreFactory.blankGroupWId;
import static ch.epfl.sweng.studdybuddy.util.CoreFactory.userGroup1;
import static org.junit.Assert.assertEquals;

public class MetaGroupAdminTest {

    private MetaGroup mg = new MetaGroup(new FirebaseReference(deepFBReference()));
    private Iterator<Pair> userGroup = userGroup1().iterator();
    private Group ghostGroup = blankGroupWId("?");
    private Group group2 = blankGroupWId("123");

    @Test
    public void rotateAdmin() {

    }

    @Test
    public void findAdminReturnsNullIfNoUserLeft() {
        assertEquals(null, mg.findNextAdmin(ghostGroup, userGroup));
    }

    @Test
    public void findAdminReturnsUpdatedGroup() {
        String previousAdminID = group2.getAdminID().toString();
        Group updated = mg.findNextAdmin(group2, userGroup);
        assertEquals("456", updated.getAdminID());
        assertEquals(previousAdminID, group2.getAdminID().toString()); //Original group left unchanged
    }

    @Test
    public void findAdminStableIfGroupNull() {
        assertEquals(null, mg.findNextAdmin(null, userGroup));
    }

    @Test
    public void findAdminStableIfIteratorEmpty() {
        assertEquals(null, mg.findNextAdmin(ghostGroup, new ArrayList<Pair>().iterator()));
    }

    @Test
    public void findAdminStableIfIteratorNull() {
        assertEquals(null, mg.findNextAdmin(ghostGroup, null));
    }

    @Test
    public void leaveGroup() {

    }
}
