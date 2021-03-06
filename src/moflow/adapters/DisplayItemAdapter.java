package moflow.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import moflow.activities.R;
import moflow.utility.AbilityScoreMod;
import moflow.utility.HitDie;
import moflow.utility.Key;
import moflow.wolfpup.Creature;

import java.util.List;

/**
 * Created by Alex on 8/8/14.
 */
public class DisplayItemAdapter extends ArrayAdapter<Creature>
{
    private Context mContext;
    private List<Creature> groupList;
    private boolean isInitiativeScreen;
    LayoutInflater inflater;

    public DisplayItemAdapter(Context context, int resource, List<Creature> objects, boolean isInitScreen)
    {
        super(context, resource, objects);
        mContext = context;
        groupList = objects;
        isInitiativeScreen = isInitScreen;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.groupitemdisplay, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        fillData(getItem(position), holder);

        return convertView;
    }

    private void fillData(Creature creature, ViewHolder holder) {
        holder.creatureName.setText( creature.getCreatureName());
        if (creature.isMonster()) {
            holder.creatureName.setTextColor(Color.RED);
        } else
            holder.creatureName.setTextColor(Color.GREEN);

        if (!isInitiativeScreen) {
            holder.creatureName.setTextColor(Color.YELLOW);
        }

        holder.armorClass.setText(creature.getArmorClass());

         // init is a special case. If it's the PC/Encounter items list show the init mod, else show the initiative.
        String dieExp = creature.getHitDie();
        String maxHP = creature.getMaxHitPoints();
        if ( !isInitiativeScreen ) {
            holder.initScore.setText(creature.getInitMod());
            holder.initLabel.setText("Init Bonus: ");

            if (HitDie.isDigit(maxHP))
                holder.maxHitPoints.setText(creature.getMaxHitPoints());
            else if (HitDie.isHitDieExpression(dieExp))
                holder.maxHitPoints.setText(creature.getHitDie());
        } else {
            holder.initScore.setText(creature.getInitiative());
            holder.initLabel.setText("Initiative: ");
            holder.maxHitPoints.setText(creature.getCurrentHitPoints() + "/" + maxHP);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean showAbilityScores = sharedPref.getBoolean(Key.PREF_SCORE, false);

        if ( showAbilityScores ) {
            String mod = AbilityScoreMod.get345AbilityScoreMod(creature.getStrength());
            mod = modToString(mod);
            holder.strScore.setText(creature.getStrength() + " (" + mod + ")");

            mod = AbilityScoreMod.get345AbilityScoreMod(creature.getDexterity());
            mod = modToString(mod);
            holder.dexScore.setText(creature.getDexterity() + " (" + mod + ")");

            mod = AbilityScoreMod.get345AbilityScoreMod(creature.getConstitution());
            mod = modToString(mod);
            holder.conScore.setText(creature.getConstitution() + " (" + mod + ")");

            mod = AbilityScoreMod.get345AbilityScoreMod(creature.getIntelligence());
            mod = modToString(mod);
            holder.intScore.setText(creature.getIntelligence() + " (" + mod + ")");

            mod = AbilityScoreMod.get345AbilityScoreMod(creature.getWisdom());
            mod = modToString(mod);
            holder.wisScore.setText(creature.getWisdom() + " (" + mod + ")");

            mod = AbilityScoreMod.get345AbilityScoreMod(creature.getCharisma());
            mod = modToString(mod);
            holder.chaScore.setText(creature.getCharisma() + " (" + mod + ")");
        } else
            holder.abilityScoresLayout.setVisibility(View.GONE);

        boolean showSavingThrows = sharedPref.getBoolean(Key.PREF_SAVETHROW, false);

        if (showSavingThrows) {
            holder.fortScore.setText(creature.getFortitude());
            holder.refScore.setText(creature.getReflex());
            holder.willScore.setText(creature.getWill());
        } else
            holder.savingThrowsLayout.setVisibility(View.GONE);

        if (creature.hasInit()) {
            holder.itemLayout.setBackgroundColor(Color.argb(77, 135, 164, 232));
        } else
            holder.itemLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    private String modToString(String modVal)
    {
        int mod = Integer.parseInt(modVal);
        return (mod >= 0) ? "+" + modVal : modVal;
    }

    private static class ViewHolder
    {
        public TextView creatureName;
        public TextView armorClass;
        public TextView maxHitPoints;
        public TextView initScore;

        // optional ability scores
        public LinearLayout abilityScoresLayout;
        public TextView strScore;
        public TextView dexScore;
        public TextView conScore;
        public TextView intScore;
        public TextView wisScore;
        public TextView chaScore;

        // optional saving throws
        public LinearLayout savingThrowsLayout;
        public TextView fortScore;
        public TextView refScore;
        public TextView willScore;

        // static labels
        public TextView initLabel;

        public LinearLayout itemLayout;

        public ViewHolder(View convertView)
        {
            creatureName = (TextView) convertView.findViewById(R.id.display_creatureName);
            armorClass   = (TextView) convertView.findViewById(R.id.display_armorClassLabel);
            maxHitPoints = (TextView) convertView.findViewById(R.id.display_maxHPLabel);
            initScore    = (TextView) convertView.findViewById(R.id.display_initBonusLabel);

            abilityScoresLayout = (LinearLayout) convertView.findViewById(R.id.display_abilityScoresLayout);
            strScore = (TextView) convertView.findViewById(R.id.display_strScore);
            dexScore = (TextView) convertView.findViewById(R.id.display_dexScore);
            conScore = (TextView) convertView.findViewById(R.id.display_conScore);
            intScore = (TextView) convertView.findViewById(R.id.display_intScore);
            wisScore = (TextView) convertView.findViewById(R.id.display_wisScore);
            chaScore = (TextView) convertView.findViewById(R.id.display_chaScore);

            savingThrowsLayout = (LinearLayout) convertView.findViewById(R.id.display_savingThrowLayout);
            fortScore = (TextView) convertView.findViewById(R.id.display_fortScore);
            refScore  = (TextView) convertView.findViewById(R.id.display_refScore);
            willScore = (TextView) convertView.findViewById(R.id.display_willScore);

            initLabel = (TextView) convertView.findViewById(R.id.display_static_initBonusLabel);

            itemLayout = (LinearLayout) convertView.findViewById(R.id.display_groupItemLayout);
        }
    }
}
