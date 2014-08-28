package moflow.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import moflow.activities.R;
import moflow.fragment.SettingsFragment;
import moflow.wolfpup.AbilityScoreMod;
import moflow.wolfpup.Creature;

import java.util.List;

/**
 * Created by Alex on 8/8/14.
 */
public class DisplayItemAdapter extends ArrayAdapter<Creature> {

    private Context mContext;
    private List<Creature> groupList;
    private boolean isInitiativeScreen;
    LayoutInflater inflater;

    public DisplayItemAdapter(Context context, int resource, List<Creature> objects, boolean isInitScreen) {
        super(context, resource, objects);
        mContext = context;
        groupList = objects;
        isInitiativeScreen = isInitScreen;
        inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    public View getView( int position, View convertView, ViewGroup parent ) {
        ViewHolder holder = null;

        if ( convertView == null ) {
            convertView = inflater.inflate( R.layout.groupitemdisplay, null );
            holder = new ViewHolder( convertView );
            convertView.setTag( holder );
        } else {
            holder = ( ViewHolder ) convertView.getTag();
        }

        fillData( getItem( position ), holder );

        return convertView;
    }

    private void fillData( Creature creature, ViewHolder holder ) {
        holder.creatureName.setText( creature.getCreatureName() );
        holder.armorClass.setText( String.valueOf(creature.getArmorClass()) );
        holder.maxHitPoints.setText( String.valueOf( creature.getMaxHitPoints() ) );

         // init is a special case. If it's the PC/Encounter items list show the init mod, else show the initiative.
        if ( !isInitiativeScreen )
            holder.initScore.setText( String.valueOf( creature.getInitMod() ) );
        else
            holder.initScore.setText( String.valueOf( creature.getInitiative() ) );

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( mContext );
        boolean showAbilityScores = sharedPref.getBoolean( SettingsFragment.ABILITY_SCORES_KEY, false  );

        if ( showAbilityScores ) {
            holder.strScore.setText( String.valueOf( creature.getStrength() ) );
            int mod = AbilityScoreMod.get345AbilityScoreMod( creature.getStrength() );
            holder.strMod.setText( ( mod >= 0 ) ? "+" + String.valueOf( mod ) : String.valueOf( mod ) );

            holder.dexScore.setText( String.valueOf( creature.getDexterity() ) );
            mod = AbilityScoreMod.get345AbilityScoreMod( creature.getDexterity() );
            holder.dexMod.setText( ( mod >= 0 ) ? "+" + String.valueOf( mod ) : String.valueOf( mod ) );

            holder.conScore.setText( String.valueOf( creature.getConstitution() ) );
            mod = AbilityScoreMod.get345AbilityScoreMod( creature.getConstitution() );
            holder.conMod.setText( ( mod >= 0 ) ? "+" + String.valueOf( mod ) : String.valueOf( mod ) );

            holder.intScore.setText( String.valueOf( creature.getIntelligence() ) );
            mod = AbilityScoreMod.get345AbilityScoreMod( creature.getIntelligence() );
            holder.intMod.setText( ( mod >= 0 ) ? "+" + String.valueOf( mod ) : String.valueOf( mod ) );

            holder.wisScore.setText( String.valueOf( creature.getWisdom() ) );
            mod = AbilityScoreMod.get345AbilityScoreMod( creature.getWisdom() );
            holder.wisMod.setText( ( mod >= 0 ) ? "+" + String.valueOf( mod ) : String.valueOf( mod ) );

            holder.chaScore.setText( String.valueOf( creature.getCharisma() ) );
            mod = AbilityScoreMod.get345AbilityScoreMod( creature.getCharisma() );
            holder.chaMod.setText( ( mod >= 0 ) ? "+" + String.valueOf( mod ) : String.valueOf( mod ) );
        } else
            holder.abilityScoresLayout.setVisibility( View.GONE );

        boolean showSavingThrows = sharedPref.getBoolean( SettingsFragment.SAVING_THROW_KEY, false );

        if ( showSavingThrows ) {
            holder.fortScore.setText( String.valueOf( creature.getFortitude() ) );
            holder.refScore.setText( String.valueOf( creature.getReflex() ) );
            holder.willScore.setText( String.valueOf( creature.getWill() ) );
        } else
            holder.savingThrowsLayout.setVisibility( View.GONE );
    }

    private static class ViewHolder {
        public TextView creatureName;
        public TextView armorClass;
        public TextView maxHitPoints;
        public TextView initScore;

        // optional ability scores
        public LinearLayout abilityScoresLayout;
        public TextView strScore;
        public TextView strMod;
        public TextView dexScore;
        public TextView dexMod;
        public TextView conScore;
        public TextView conMod;
        public TextView intScore;
        public TextView intMod;
        public TextView wisScore;
        public TextView wisMod;
        public TextView chaScore;
        public TextView chaMod;

        // optional saving throws
        public LinearLayout savingThrowsLayout;
        public TextView fortScore;
        public TextView refScore;
        public TextView willScore;

        public ViewHolder( View convertView ) {
            creatureName = ( TextView ) convertView.findViewById(R.id.display_creatureName);
            creatureName.setTextColor( Color.GREEN );
            armorClass = ( TextView ) convertView.findViewById( R.id.display_armorClassLabel );
            maxHitPoints = ( TextView ) convertView.findViewById( R.id.display_maxHPLabel );
            initScore = ( TextView ) convertView.findViewById( R.id.display_initBonusLabel );

            abilityScoresLayout = ( LinearLayout ) convertView.findViewById( R.id.display_abilityScoresLayout );
            strScore = ( TextView ) convertView.findViewById( R.id.display_strScore );
            strMod = ( TextView ) convertView.findViewById( R.id.display_strMod );
            dexScore = ( TextView ) convertView.findViewById( R.id.display_dexScore );
            dexMod = ( TextView ) convertView.findViewById( R.id.display_dexMod );
            conScore = ( TextView ) convertView.findViewById( R.id.display_conScore );
            conMod = ( TextView ) convertView.findViewById( R.id.display_conMod );
            intScore = ( TextView ) convertView.findViewById( R.id.display_intScore );
            intMod = ( TextView ) convertView.findViewById( R.id.display_intMod );
            wisScore = ( TextView ) convertView.findViewById( R.id.display_wisScore );
            wisMod = ( TextView ) convertView.findViewById( R.id.display_wisMod );
            chaScore = ( TextView ) convertView.findViewById( R.id.display_chaScore );
            chaMod = ( TextView ) convertView.findViewById( R.id.display_chaMod );

            savingThrowsLayout = ( LinearLayout ) convertView.findViewById( R.id.display_savingThrowLayout );
            fortScore = ( TextView ) convertView.findViewById( R.id.display_fortScore );
            refScore = ( TextView ) convertView.findViewById( R.id.display_refScore );
            willScore = ( TextView ) convertView.findViewById( R.id.display_willScore );
        }
    }
}
