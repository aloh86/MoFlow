package moflow.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.*;
import moflow.activities.R;
import moflow.utility.HitDie;
import moflow.utility.HitDieInputFilter;
import moflow.utility.Key;
import moflow.wolfpup.Creature;

/**
 * Created by Alex on 8/28/14.
 * I must admit this class is a bit monolithic since this dialog
 * uses a layout that is used for three different activities, but it prevents alot of
 * code from being duplicated in what would otherwise be three very similar dialog classes.
 *
 * The views hidden or shown, and values retrieved or set depend on whether
 * this dialog is being used to create or edit a new creature dialog in the
 * group item edit activity, catalog activity, or initiative activity.
 */
public class CreatureEditDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private SimpleDialogListener simpleDialogListener;

    private String title;
    private String usage;

    private EditText creatureName;
    private EditText armorClass;
    private EditText maxHP;
    private EditText initiative;
    private EditText strength;
    private EditText dexterity;
    private EditText constitution;
    private EditText intelligence;
    private EditText wisdom;
    private EditText charisma;
    private EditText fort;
    private EditText ref;
    private EditText will;
    private EditText hitPointMod;

    private TextView initiativeLabel;

    private Switch creatureTypeSwitch;
    private Switch hitPointModTypeSwitch;

    private LinearLayout abilityScoresLayout;
    private LinearLayout savingThrowsLayout;
    private LinearLayout creatureTypeLayout;
    private LinearLayout hitPointModLayout;

    private boolean showAbilityScores;
    private boolean showSavingThrows;
    private boolean usedForGroupEditActivity;
    private boolean usedForCatalogActivity;
    private boolean usedForInitNewCreature;
    private boolean usedForInitEditCreature;

    private Creature critter;

    private View view;

    public static CreatureEditDialog newInstance(String dialogTitle, Creature critter, String usageVal) {
        CreatureEditDialog ced = new CreatureEditDialog();
        Bundle args = new Bundle();
        args.putString(Key.DIALOG_TITLE, dialogTitle);
        args.putParcelable(Key.CREATURE_OBJECT, critter);
        args.putString(Key.EDIT_CREATURE_DIALOG_USAGE, usageVal);
        ced.setArguments(args);

        return ced;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        title = bundle.getString(Key.DIALOG_TITLE);
        critter = bundle.getParcelable(Key.CREATURE_OBJECT);
        usage = bundle.getString(Key.EDIT_CREATURE_DIALOG_USAGE);

        if (usage.equals(Key.Val.EDITGROUP_ACTIVITY)) {
            usedForGroupEditActivity = true;
        } else if (usage.equals(Key.Val.CATALOG_ACTIVITY)) {
            usedForCatalogActivity = true;
        } else if (usage.equals(Key.Val.USAGE_INIT_NEW_CREATURE)) {
            usedForInitNewCreature = true;
        } else if (usage.equals(Key.Val.USAGE_INIT_EDIT_CREATURE)) {
            usedForInitEditCreature = true;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createView());
        builder.setTitle(title);
        builder.setPositiveButton("Ok", this);
        builder.setNegativeButton("Cancel", this);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            simpleDialogListener = (SimpleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SimpleDialogListener");
        }
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int choice) {
        if (choice == DialogInterface.BUTTON_POSITIVE)
            simpleDialogListener.onDialogPositiveClick(this);
        else
            simpleDialogListener.onDialogNegativeClick(this);
    }

    private View createView() {
        view = getActivity().getLayoutInflater().inflate(R.layout.groupitemedit, null);

        creatureName = (EditText) view.findViewById(R.id.creatureNameEditText);
        armorClass = (EditText) view.findViewById(R.id.creatureArmorClassEditText);

        hitPointMod = (EditText) view.findViewById(R.id.creatureHitPointModEditText);
        maxHP = (EditText) view.findViewById(R.id.creatureMaxHitPointsEditText);

        initiativeLabel = (TextView) view.findViewById(R.id.initiativeLabel);
        initiative = ( EditText ) view.findViewById( R.id.creatureInitiativeEditText);

        creatureTypeSwitch = (Switch) view.findViewById(R.id.creatureTypeSwitch);
        hitPointModTypeSwitch = (Switch) view.findViewById(R.id.hitPointModTypeSwitch);

        strength = ( EditText ) view.findViewById( R.id.creatureStrEditText );
        dexterity = ( EditText ) view.findViewById( R.id.creatureDexEditText );
        constitution = ( EditText ) view.findViewById( R.id.creatureConEditText );
        intelligence = ( EditText ) view.findViewById( R.id.creatureIntEditText );
        wisdom = ( EditText ) view.findViewById( R.id.creatureWisEditText );
        charisma = ( EditText ) view.findViewById(  R.id.creatureChaEditText );

        fort = ( EditText ) view.findViewById( R.id.creatureFortEditText );
        ref = ( EditText ) view.findViewById( R.id.creatureRefEditText );
        will = ( EditText ) view.findViewById( R.id.creatureWillEditText );

        abilityScoresLayout = ( LinearLayout ) view.findViewById( R.id.edit_abilityScoresLayout );
        savingThrowsLayout = ( LinearLayout ) view.findViewById( R.id.edit_savingThrowsLayout );
        creatureTypeLayout = (LinearLayout) view.findViewById(R.id.creatureTypeLayout);
        hitPointModLayout = (LinearLayout) view.findViewById(R.id.hitPointModLayout);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( getActivity().getApplicationContext() );
        showAbilityScores = sharedPref.getBoolean( Key.PREF_SCORE, false  );
        showSavingThrows = sharedPref.getBoolean( Key.PREF_SAVETHROW, false );

        if ( !showAbilityScores )
            abilityScoresLayout.setVisibility( View.GONE );
        if ( !showSavingThrows )
            savingThrowsLayout.setVisibility( View.GONE );

        if ( critter != null ) {
            setFields();
        }

        // Change or Hide some of the views depending on the usage
        // (e.g. from catalog, party/encounter manager, initiative)
        if (usedForGroupEditActivity || usedForCatalogActivity) {
            creatureTypeLayout.setVisibility(View.GONE);
            hitPointModLayout.setVisibility(View.GONE);
        } else if (usedForInitNewCreature) {
            hitPointModLayout.setVisibility(View.GONE);
        } else if (usedForInitEditCreature) {
            creatureName.setVisibility(View.GONE);
            creatureTypeLayout.setVisibility(View.GONE);
            initiativeLabel.setText(getString(R.string.initiativeLabel));
            //maxHitPointLayout.setVisibility(View.GONE);
        }

        if (usedForInitEditCreature) {
            maxHP.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else {
            //maxHP.setKeyListener(DigitsKeyListener.getInstance("0123456789d+-"));
            HitDieInputFilter hdFilter = new HitDieInputFilter();
            maxHP.setFilters(new InputFilter[]{hdFilter});
            maxHP.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        return view;
    }

    // Set the fields if being used for editing an existing creature.
    private void setFields() {
        armorClass.setText(critter.getArmorClass());

        if (usedForGroupEditActivity || usedForCatalogActivity) {
            initiative.setText(critter.getInitMod());
            creatureName.setText(critter.getCreatureName());
        } else if (usedForInitEditCreature) {
            initiative.setText(critter.getInitiative());
        }

        if (!usedForInitEditCreature) {
            boolean hasHitDie = HitDie.isHitDieExpression(critter.getHitDie());
            if (hasHitDie) {
                maxHP.setText(critter.getHitDie());
            } else
                maxHP.setText(critter.getMaxHitPoints());
        } else {
            maxHP.setText(critter.getMaxHitPoints());
        }

        strength.setText(critter.getStrength());
        dexterity.setText(critter.getDexterity());
        constitution.setText(critter.getConstitution());
        intelligence.setText(critter.getIntelligence());
        wisdom.setText(critter.getWisdom());
        charisma.setText(critter.getCharisma());

        fort.setText(critter.getFortitude());
        ref.setText(critter.getReflex());
        will.setText(critter.getWill());
    }

    // Gets the new stats for the creature when positive dialog button is pressed.
    public Creature getCritter() {
        Creature thing = new Creature();

        if (usedForInitEditCreature) {
            thing.setCreatureName(critter.getCreatureName());
        } else {
            thing.setCreatureName(creatureName.getText().toString().trim());
        }

        thing.setArmorClass(armorClass.getText().toString());

        String hp = maxHP.getText().toString();
        if (usedForGroupEditActivity || usedForCatalogActivity || usedForInitNewCreature) {
            if (!HitDie.isHitDieExpression(hp) && !HitDie.isDigit(hp)) {
                return null;
            }
            thing.setInitMod(initiative.getText().toString());
        } else if (usedForInitEditCreature) {
            if (!HitDie.isDigit(hp)) {
                return null;
            }

            String init = initiative.getText().toString();
            String initMod = critter.getInitMod();
            int initSum = Integer.valueOf(init) + Integer.valueOf(initMod);
            String initString = String.valueOf(initSum);
            thing.setInitiative(initString);

            // Change the current hit point value if a life gain/loss value was supplied
            String curHP = this.critter.getCurrentHitPoints();
            if (!curHP.isEmpty()) {
                int currentHP = Integer.valueOf(curHP);
                String hpMod = hitPointMod.getText().toString();
                hpMod = (hpMod.isEmpty() ? "0" : hpMod);
                int hpModValue = (hpMod == null ? 0 : Integer.valueOf(hpMod));

                if (hitPointModTypeSwitch.isChecked()) {
                    currentHP += hpModValue;
                } else {
                    currentHP -= hpModValue;
                }
                curHP = String.valueOf(currentHP);
                thing.setCurrentHitPoints(curHP);
            }
        }

        if (usedForInitNewCreature) {
            if (!creatureTypeSwitch.isChecked()) {
                thing.setAsMonster(true);
            } else {
                thing.setAsMonster(false);
            }
        }

        boolean hasHitDie = HitDie.isHitDieExpression(hp);
        if (hasHitDie) {
            thing.setHitDie(hp);
        } else {
            thing.setMaxHitPoints(hp);

            if (usedForInitNewCreature) {
                thing.setCurrentHitPoints(hp);
            }
        }

        if (showAbilityScores) {
            thing.setStrength(strength.getText().toString());
            thing.setDexterity(dexterity.getText().toString());
            thing.setConstitution(constitution.getText().toString());
            thing.setIntelligence(intelligence.getText().toString());
            thing.setWisdom(wisdom.getText().toString());
            thing.setCharisma(charisma.getText().toString());
        }

        if (showSavingThrows) {
            thing.setFortitude(fort.getText().toString());
            thing.setReflex(ref.getText().toString());
            thing.setWill(will.getText().toString());
        }

        return thing;
    }

    /**
     * Make sure fields are not empty.
     * @return true if any fields are empty, false otherwise
     */
    public boolean hasEmptyFields() {
        boolean valid = false;

        if (usedForGroupEditActivity || usedForCatalogActivity || usedForInitNewCreature ) {
            if (creatureName.getText().toString().isEmpty())
                valid = true;
        }

        if (armorClass.getText().toString().isEmpty() ||
                maxHP.getText().toString().isEmpty() ||
                initiative.getText().toString().isEmpty())
            valid = true;

        if (showAbilityScores) {
            if (strength.getText().toString().isEmpty() ||
                    dexterity.getText().toString().isEmpty() ||
                    constitution.getText().toString().isEmpty() ||
                    intelligence.getText().toString().isEmpty() ||
                    wisdom.getText().toString().isEmpty() ||
                    charisma.getText().toString().isEmpty())
                valid = true;
        }

        if (showSavingThrows) {
            if (fort.getText().toString().isEmpty() ||
                    ref.getText().toString().isEmpty() ||
                    will.getText().toString().isEmpty())
                valid = true;
        }
        return valid;
    }
}
