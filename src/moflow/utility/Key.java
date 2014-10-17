package moflow.utility;

/**
 * Created by Alex on 6/3/14.
 */

/**
 * Keys and values that are used multiple times across different activities.
 */
public class Key {
    public static final String GROUP_TYPE = "groupType";
    public static final String GROUP_NAME = "groupName";
    public static final String PARENT_ACTIVITY = "parentActivity";
    public static final String PREF_SCORE = "pref_score";
    public static final String PREF_SAVETHROW = "pref_saveThrow";
    public static final String PREF_CATALOG_VERSION = "pref_catalogEdition";
    public static final String CREATURE_OBJECT = "creature_object";
    public static final String NUM__CREATURE_PICKED = "num_creature_picked";
    public static final String NUMPICK_CATALOG_CREATURE_BUNDLE = "creature_object_bundle";
    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String TRUE_FALSE = "boolean";

    public static final int PICK_CREATURE = 1;

    public static class Val {
        public static final String PARTY = "party";
        public static final String ENCOUNTER = "encounter";
        public static final String FROM_MAIN = "mainMenuActivity";
        public static final String FROM_INIT = "initiativeActivity";
        public static final String FROM_GROUP_ITEM = "encounterActivity";
    }
}
