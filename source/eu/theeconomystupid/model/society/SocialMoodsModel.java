package eu.theeconomystupid.model.society;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public final class SocialMoodsModel extends Operator {
    
    
    private static final String ANGRY = ".angry.";
    private static final String UNHAPPY = ".unhappy.";
    private static final String HAPPY = ".happy.";
    
    private static final String HIGH = "_high";
    private static final String LOW = "_low";
    
    private static final String INFLATION_RATE = "inflation_rate";
    private static final String UNEMPLOYMENT_RATE = "unemployment_rate";
    private static final String PUBLIC_SECTOR_EFFECTIVENESS = "public_sector_effectiveness";
    private static final String INCOME_TAX_RATE = "income_tax_rate";
    private static final String VAT_RATE = "vat_rate";
    private static final String INCOME = "income";
    
    private static final String DEMONSTRATIONS = "demonstrations";
    
    private String inflationRateKey;
    private String unemploymentRateKey;
    private String publicSectorEffectivenessKey;
    private String vatRateKey;
    private String incomeTaxRateKey;
    private String approvalRatingKey;
    private String socialMoodsKey;
    private String eventsKey;
    
    private OpinionGroup[] opinionGroups;
    
    private double approvalRating;
    private double inflationRate;
    private double dInflationRate;
    private double unemploymentRate;
    private double dUnemploymentRate;
    private double publicSectorEffectiveness;
    private double dPublicSectorEffectiveness;
    private double vatRate;
    private double dVatRate;
    private double incomeTaxRate;
    private double dIncomeTaxRate;
    private String[] events;
    
    private double baseOpinion_ir;
    private double baseOpinion_ur;
    private double baseOpinion_pse;
    private double baseOpinion_itr;
    private double baseOpinion_vr;
    private double baseOpinion;
    private double baseOpinionWeight;
    
    private String[] socialMoods;
    private List <String> populationMoodsList;
    private List <String> eventsList;

    private double ir_a;
    private double ur_a;
    private double pse_a;
    private double vr_a;
    private double itr_a;
    private double agi_a;
    
    private double happinessThreashold;
    private double unhappinessThreashold;    
    private double angerThreashold;
    private double baseDemonstrationPropensity;
    
    private long seed;
    private Random random;
    
    
    public SocialMoodsModel() {}
        
    public void configure( Registry r, Map <String,String> config ) {
        inflationRateKey = config.get( "inflationRateKey" );
        unemploymentRateKey = config.get( "unemploymentRateKey" );
        publicSectorEffectivenessKey = config.get( "publicSectorEffectivenessKey" );
        vatRateKey = config.get( "vatRateKey" );
        incomeTaxRateKey = config.get( "incomeTaxRateKey" );
        approvalRatingKey = config.get( "approvalRatingKey" );
        socialMoodsKey = config.get( "socialMoodsKey" );
        eventsKey = config.get( "eventsKey" );
        
        int numGroups = Integer.parseInt( config.get( "numberOfGroups" ) );
        opinionGroups = new OpinionGroup[numGroups];
        for ( int i=0; i<numGroups; i++ ) {
            opinionGroups[i] = createOpinionGroup( config, i );
        }
        
        inflationRate = r.getDouble( inflationRateKey );
        unemploymentRate = r.getDouble( unemploymentRateKey );
        publicSectorEffectiveness = r.getDouble( publicSectorEffectivenessKey );
        vatRate = r.getDouble( vatRateKey );
        incomeTaxRate = r.getDouble( incomeTaxRateKey );        
        
        ir_a = Double.parseDouble( config.get("ir_a") );
        ur_a = Double.parseDouble( config.get("ur_a") );
        pse_a = Double.parseDouble( config.get("pse_a") );
        itr_a = Double.parseDouble( config.get("itr_a") );
        vr_a = Double.parseDouble( config.get("vr_a") );
        agi_a = Double.parseDouble( config.get("agi_a") );

        baseOpinion_ir = Double.parseDouble( config.get( "baseOpinion_ir" ) );
        baseOpinion_ur = Double.parseDouble( config.get( "baseOpinion_ur" ) );
        baseOpinion_pse = Double.parseDouble( config.get( "baseOpinion_pse" ) );
        baseOpinion_itr = Double.parseDouble( config.get( "baseOpinion_itr" ) );
        baseOpinion_vr = Double.parseDouble( config.get( "baseOpinion_vr" ) );
        
        baseOpinionWeight = Double.parseDouble( config.get("baseOpinionWeight") );
        angerThreashold = Double.parseDouble( config.get("angerThreashold") );
        unhappinessThreashold = Double.parseDouble( config.get("unhappinessThreashold") );
        happinessThreashold = Double.parseDouble( config.get("happinessThreashold") );
        baseDemonstrationPropensity = Double.parseDouble( config.get("baseDemonstrationPropensity") );
        seed = Long.parseLong( config.get("seed") );
        
        if ( seed == 0 ) seed = System.currentTimeMillis();
        random = new Random( seed );       
        
        baseOpinion = ( baseOpinion_ir + baseOpinion_itr + baseOpinion_pse + baseOpinion_ur + baseOpinion_vr ) / 5.0;
        
        for ( OpinionGroup og : opinionGroups ) {
            og.numPeople = r.getDouble( og.numPeopleKey );
            og.realAveGrossIncome = r.getDouble( og.realAveGrossIncomeKey );
            og.approvalRating = r.getDouble( og.approvalRatingKey );
            og.opinion_agi = (og.approvalRating - baseOpinionWeight*baseOpinion)/(1.0-baseOpinionWeight);
        }
        
        populationMoodsList = new ArrayList <String> ();
        eventsList = new ArrayList <String> ();
        
    }
    
    public void execute( Registry r ) {
        readInputData(r);
        calculateBaseOpinion();
        calculateGroupOpinions();
        calculateApprovalRating();
        updateSocietyMoodsAndEventsArrays();
        writeOutputData(r);
    }
    
    
    // ****************************************************
    // ***************** PRIVATE METHODS ******************
    // ****************************************************
    private void readInputData( Registry r ) {
        double inflationRate_0 = inflationRate;
        double unemploymentRate_0 = unemploymentRate;
        double publicSectorEffectiveness_0 = publicSectorEffectiveness;
        double vatRate_0 = vatRate;
        double incomeTaxRate_0 = incomeTaxRate;
        
        inflationRate = r.getDouble( inflationRateKey );
        unemploymentRate = r.getDouble( unemploymentRateKey );
        publicSectorEffectiveness = r.getDouble( publicSectorEffectivenessKey );
        vatRate = r.getDouble( vatRateKey );
        incomeTaxRate = r.getDouble( incomeTaxRateKey );
        events = (String[]) r.get( eventsKey );
        eventsList.clear();
        if ( events != null ) {
            for ( String s : events ) eventsList.add(s);
        }
        
        dInflationRate = inflationRate - inflationRate_0;
        dUnemploymentRate = unemploymentRate - unemploymentRate_0;
        dPublicSectorEffectiveness = publicSectorEffectiveness - publicSectorEffectiveness_0;
        dIncomeTaxRate = incomeTaxRate - incomeTaxRate_0;
        dVatRate = vatRate - vatRate_0;
        
        double realAveGrossIncome_0;
        double realAveGrossIncome_1;
        for ( OpinionGroup og : opinionGroups ) {
            realAveGrossIncome_0 = og.realAveGrossIncome;
            realAveGrossIncome_1 = r.getDouble( og.realAveGrossIncomeKey );
            og.numPeople = r.getDouble( og.numPeopleKey );
            og.realAveGrossIncome = realAveGrossIncome_1;
            if ( realAveGrossIncome_0 > 0.0 ) {
                og.dRealAveGrossIncome = realAveGrossIncome_1-realAveGrossIncome_0;
            } else {
                og.dRealAveGrossIncome = 1.0;  // Assume 100% change
            }
        }
        
    }
    
    private void calculateBaseOpinion() {

        double dBaseOpinion_ir = ir_a * dInflationRate;        
        baseOpinion_ir += dBaseOpinion_ir;
//        baseOpinion_ir = trimOpinion( (1.0-opinionPersistance)*0.5 + opinionPersistance*baseOpinion_ir );                
        
        double dBaseOpinion_ur = ur_a * dUnemploymentRate;
        baseOpinion_ur += dBaseOpinion_ur;
//        baseOpinion_ur = trimOpinion( (1.0-opinionPersistance)*0.5 + opinionPersistance*baseOpinion_ur );
        
        double dBaseOpinion_pse = pse_a * dPublicSectorEffectiveness;
        baseOpinion_pse += dBaseOpinion_pse;
//        baseOpinion_pse = trimOpinion( (1.0-opinionPersistance)*0.5 + opinionPersistance*baseOpinion_pse );
        
        double dBaseOpinion_itr = itr_a * dIncomeTaxRate;
        baseOpinion_itr += dBaseOpinion_itr;
//        baseOpinion_itr = trimOpinion( (1.0-opinionPersistance)*0.5 + opinionPersistance*baseOpinion_itr );
        
        double dBaseOpinion_vr = vr_a * dVatRate;
        baseOpinion_vr += dBaseOpinion_vr;
//        baseOpinion_vr = trimOpinion( (1.0-opinionPersistance)*0.5 + opinionPersistance*baseOpinion_vr );
        
//        baseOpinion = trimOpinion(( baseOpinion_ir + baseOpinion_itr + baseOpinion_pse + baseOpinion_ur + baseOpinion_vr ) / 5);
        baseOpinion = ( baseOpinion_ir + baseOpinion_itr + baseOpinion_pse + baseOpinion_ur + baseOpinion_vr ) / 5.0;

    }
    
    private void calculateGroupOpinions() {
        double dOpinion_agi;
        for ( OpinionGroup og : opinionGroups ) {
            dOpinion_agi = agi_a * og.dRealAveGrossIncome;
            og.opinion_agi += dOpinion_agi;
//            og.opinion_agi = trimOpinion( og.opinion_agi );
        }        
    }
    
    private void calculateApprovalRating() {
        double s = 0.0;
        int sumPeople = 0;
        for ( OpinionGroup og : opinionGroups ) {
            og.approvalRating = opinionToApprovalRating( og.opinion_agi );
            s += og.numPeople*og.approvalRating;
            sumPeople += og.numPeople;
        }
        approvalRating = trimOpinion(s/sumPeople);
    }
    
    private void updateSocietyMoodsAndEventsArrays() {
        populationMoodsList.clear();
        
        boolean demonstrations = random.nextDouble() < baseDemonstrationPropensity;
        appendToLists( "all", INFLATION_RATE, baseOpinion_ir, true, demonstrations );
        appendToLists( "all", UNEMPLOYMENT_RATE, baseOpinion_ur,  true, demonstrations );
        appendToLists( "all", PUBLIC_SECTOR_EFFECTIVENESS, baseOpinion_pse, false, demonstrations );
        appendToLists( "all", INCOME_TAX_RATE, baseOpinion_itr, true, demonstrations );
        appendToLists( "all", VAT_RATE, baseOpinion_vr, true, demonstrations );
        
        for ( OpinionGroup og : opinionGroups ) {
            demonstrations = random.nextDouble() < og.demonstrationPropensity;
            appendToLists( og.id, INCOME, og.opinion_agi, false, demonstrations );
        }
        
        int size = populationMoodsList.size();
        if ( size == 0 ) {
            socialMoods = null;
        } else {
            socialMoods = populationMoodsList.toArray( new String[size] );
        }
        
        size = eventsList.size();
        if ( size == 0 ) {
            events = null;
        } else {
            events = eventsList.toArray( new String[size] );
        }
        
    }
    
    private void writeOutputData( Registry r ) {
        r.put( approvalRatingKey, approvalRating );
        r.put( socialMoodsKey, socialMoods );
        r.put( eventsKey, events );
        for ( OpinionGroup og : opinionGroups ) {
            r.put( og.approvalRatingKey, og.approvalRating );
        }
    }

    
    // ****************************************************
    // ***************** UTILITY METHODS ******************
    // ****************************************************
    private double opinionToApprovalRating( double opinion ) {
        double groupOpinion = trimOpinion( baseOpinionWeight*baseOpinion + (1.0-baseOpinionWeight)*opinion );
        double groupApprovalRating = Math.min( 0.92, Math.max( 0.01, groupOpinion + 0.05*(random.nextDouble()-0.5) ) );
        return groupApprovalRating;
    }
    
    private void appendToLists( String group, String issue, double op, boolean downwardPositive, boolean demonstrations ) {
        if ( op < angerThreashold ) {            
            if ( downwardPositive ) {
                populationMoodsList.add( group + ANGRY + issue + HIGH );
                if ( demonstrations ) {
                    eventsList.add( DEMONSTRATIONS + "." + group + "." + issue + HIGH );
                }
            } else {
                populationMoodsList.add( group + ANGRY + issue + LOW );
                if ( demonstrations ) {
                    eventsList.add( DEMONSTRATIONS + "." + group + "." + issue + LOW );
                }
            }
        } else if ( op < unhappinessThreashold ) {
            if ( downwardPositive ) {
                populationMoodsList.add( group + UNHAPPY + issue + HIGH );
            } else {
                populationMoodsList.add( group + UNHAPPY + issue + LOW );
            }
        } else if ( op > happinessThreashold ) {
            if ( downwardPositive ) {
                populationMoodsList.add( group + HAPPY + issue + LOW );
            } else {
                populationMoodsList.add( group + HAPPY + issue + HIGH );
            }
        }
    }

    private static double trimOpinion( double value ) {
        if ( value > 1.0 ) {
            return 1.0;
        } else if ( value < 0.0 ) {
            return 0.0;
        } else {
            return value;
        }
    }
    
    private static OpinionGroup createOpinionGroup( Map <String,String> config, int iGroup ) {
        String preffix = "group[" + iGroup + "].";
        return new OpinionGroup(
            config.get( preffix + "id" ),
            config.get( preffix + "npKey" ),
            config.get( preffix + "ragiKey" ),
            config.get( preffix + "apKey" ),
            Double.parseDouble( config.get( preffix + "dp" ) )
        );
    }
    
    
    // ****************************************************
    // *************** OPINION GROUP CLASS ****************
    // ****************************************************
    private static final class OpinionGroup {
        
        private String id;
        private String numPeopleKey;
        private String realAveGrossIncomeKey;
        private String approvalRatingKey;
        private double numPeople;
        private double realAveGrossIncome;
        private double dRealAveGrossIncome;
        private double opinion_agi;
        private double approvalRating;
        private double demonstrationPropensity;
        
        private OpinionGroup( String id, String numPeopleKey, String realAveGrossIncomeKey, String approvalRatingKey, double demonstrationPropensity ) {
            this.id = id;
            this.numPeopleKey = numPeopleKey;
            this.realAveGrossIncomeKey = realAveGrossIncomeKey;
            this.approvalRatingKey = approvalRatingKey;
            this.demonstrationPropensity = demonstrationPropensity;
        }
        
    }
    
}