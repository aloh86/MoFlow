package moflow.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import moflow.activities.R;
import moflow.utility.CommonKey;
import moflow.wolfpup.Creature;

/**
 * Created by Alex on 8/28/14.
 */
public class CreatureEditDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private SimpleDialogListener simpleDialogListener;
    private String title;

    private EditText creatureName;
    private EditText armorClass;
    private EditText maxHP;
    private EditText initBonus;
    private EditText strength;
    private EditText dexterity;
    private EditText constitution;
    private EditText intelligence;
    private EditText wisdom;
    private EditText charisma;
    private EditText fort;
    private EditText ref;
    private EditText will;

    private LinearLayout abilityScoresLayout;
    private LinearLayout savingThrowsLayout;

    private boolean showAbilityScores;
    private boolean showSavingThrows;

    private Creature critter;

    private View view;

    public CreatureEditDialog( String dialogTitle ) {
        title = dialogTitle;
        critter = null;
    }

    public CreatureEditDialog( String dialogTitle, Creature someCritter ) {
        title = dialogTitle;
        critter = someCritter;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setView( createView() );
        builder.setTitle( title );
        builder.setPositiveButton( "Ok", this );
        builder.setNegativeButton( "Cancel", this );
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside( false );

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
    public void onClick( DialogInterface dialogInterface, int choice ) {
        if ( choice == DialogInterface.BUTTON_POSITIVE )
            simpleDialogListener.onDialogPositiveClick( this );
        else
            simpleDialogListener.onDialogNegativeClick( this );
    }

    private View createView() {
        view = getActivity().getLayoutInflater().inflate( R.layout.groupitemedit, null );

        creatureName = ( EditText ) view.findViewById( R.id.creatureNameEditText );
        armorClass = ( EditText ) view.findViewById( R.id.creatureArmorClassEditText );
        maxHP = ( EditText ) view.findViewById( R.id.creatureMaxHitPointsEditText );
        initBonus = ( EditText ) view.findViewById( R.id.creatureInitBonusEditText );

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( getActivity().getApplicationContext() );
        showAbilityScores = sharedPref.getBoolean( CommonKey.ABILITY_SCORES_KEY, false  );
        showSavingThrows = sharedPref.getBoolean( CommonKey.SAVING_THROW_KEY, false );

        if ( !showAbilityScores )
            abilityScoresLayout.setVisibility( View.GONE );
        if ( !showSavingThrows )
            savingThrowsLayout.setVisibility( View.GONE );

        if ( critter != null ) {
            setFields();
        }

        return view;
    }

    private void setFields() {
        creatureName.setText( critter.getCreatureName() );
        armorClass.setText(critter.getArmorClass());
        maxHP.setText(critter.getMaxHitPoints());
        initBonus.setText(critter.getInitMod());

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

    // Gets the new stats for the creature when dialog is used for editing an existing creature.
    public Creature getCritter() {
        Creature thing = new Creature();

        thing.setCreatureName( creatureName.getText().toString().trim() );
        thing.setArmorClass(armorClass.getText().toString());
        thing.setMaxHitPoints(maxHP.getText().toString());
        thing.setInitMod(initBonus.getText().toString());

        if ( showAbilityScores ) {
            thing.setStrength(strength.getText().toString());
            thing.setDexterity(dexterity.getText().toString());
            thing.setConstitution(constitution.getText().toString());
            thing.setIntelligence(intelligence.getText().toString());
            thing.setWisdom(wisdom.getText().toString());
            thing.setCharisma(charisma.getText().toString());
        }

        if ( showSavingThrows ) {
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
    public boolean isEmptyFields() {
        boolean valid = false;

        if ( creatureName.getText().toString().isEmpty() ||
                armorClass.getText().toString().isEmpty() ||
                maxHP.getText().toString().isEmpty() ||
                initBonus.getText().toString().isEmpty() )
            valid = true;

        if ( showAbilityScores ) {
            if ( strength.getText().toString().isEmpty() ||
                    dexterity.getText().toString().isEmpty() ||
                    constitution.getText().toString().isEmpty() ||
                    intelligence.getText().toString().isEmpty() ||
                    wisdom.getText().toString().isEmpty() ||
                    charisma.getText().toString().isEmpty() )
                valid = true;
        }

        if ( showSavingThrows ) {
            if ( fort.getText().toString().isEmpty() ||
                    ref.getText().toString().isEmpty() ||
                    will.getText().toString().isEmpty() )
                valid = true;
        }

        return valid;
    }
}
