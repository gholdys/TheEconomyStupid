package eu.theeconomystupid.engine;

public final class Keys {
    
    public static final Key GDP = new Key("economics.gdp");
    public static final Key GDP_GROWTH_RATE = new Key("economics.gdpGrowthRate");
    public static final Key REAL_OUTPUT = new Key("economics.realOutput");
    public static final Key REAL_CONSUMPTION = new Key("economics.realConsumption");
    public static final Key REAL_INVESTMENT = new Key("economics.realInvestment");
    public static final Key REAL_EXPORTS = new Key("economics.realExports");
    public static final Key REAL_IMPORTS = new Key("economics.realImports");
    public static final Key REAL_GOVERNMENT_SPENDING = new Key("economics.realGovernmentSpending");    
    public static final Key REAL_TAX_REVENUES = new Key("economics.realTaxRevenues");
    public static final Key OUTPUT = new Key("economics.output");
    public static final Key EXPORTS = new Key("economics.exports");
    public static final Key IMPORTS = new Key("economics.imports");
    public static final Key CONSUMPTION = new Key("economics.consumption");
    public static final Key INVESTMENT = new Key("economics.investment");
    public static final Key GOVERNMENT_SPENDING = new Key("economics.governmentSpending");
    public static final Key TAX_REVENUES = new Key("economics.taxRevenues");
    public static final Key WORKFORCE = new Key("economics.workforce");
    public static final Key UNEMPLOYMENT_RATE = new Key("economics.unemploymentRate");
    public static final Key INFLATION_RATE = new Key("economics.inflationRate");
    public static final Key INTEREST_RATE = new Key("economics.interestRate");
    public static final Key BUDGET_DEFICIT = new Key("economics.budgetDeficit");
    public static final Key NATIONAL_DEBT = new Key("economics.nationalDebt");
    public static final Key NATIONAL_DEBT_TO_GDP = new Key("economics.nationalDebtToGdp");    
    public static final Key GLOBAL_ECONOMY_CONDITION = new Key("economics.globalEconomyCondition");
    public static final Key PUBLIC_SECTOR_EFFECTIVENESS = new Key("publicSector.effectiveness");
    public static final Key PUBLIC_SECTOR_OPTIMUM_EMPLOYMENT_TO_POPULATION = new Key("publicSector.optimumEmploymentToPopulation");
    public static final Key PUBLIC_SECTOR_OPTIMUM_FUNDING_TO_GDP = new Key("publicSector.optimumFundingToGDP");
    public static final Key APPROVAL_RATING = new Key("society.approvalRating");
    public static final Key TOTAL_POPULATION = new Key("society.totalPopulation");
    public static final Key MATURITY_AGE = new Key("society.maturityAge");
    public static final Key MAXIMUM_AGE = new Key("society.maximumAge");
    public static final Key SOCIETY_MOODS = new Key("society.moods");    
    
    public static final Key GAME_TIME = new Key("game.time");    
    public static final Key TURN_NUMBER = new Key("game.turn");
    public static final Key NUM_TURNS_TO_ELECTIONS = new Key("game.numTurnsToNextElections");
    public static final Key NUM_MONTHS_PER_TURN = new Key("game.numMonthsPerTurn");
    public static final Key TERM_LENGTH = new Key("game.termLength");
    public static final Key MAX_NATIONAL_DEBT_TO_GDP = new Key("game.maxNationalDebtToGDP");    
    public static final Key ELECTIONS_RESULT = new Key("game.electionsResult");
    public static final Key EVENTS = new Key("game.events");
    
    public static final PolicyKey VAT = new PolicyKey("policies.vat");
    public static final PolicyKey INCOME_TAX = new PolicyKey("policies.incomeTax");
    public static final PolicyKey UNEMPLOYMENT_BENEFITS = new PolicyKey("policies.unemploymentBenefits");
    public static final PolicyKey RETIREMENT_BENEFITS = new PolicyKey("policies.retirementBenefits");
    public static final PolicyKey PUBLIC_SECTOR_FUNDING = new PolicyKey("policies.publicSectorFunding");
    
    public static final SocialGroupKey PRIVATE_SECTOR_EMPLOYEES = new SocialGroupKey("socialGroups.privateSectorEmployees");
    public static final SocialGroupKey PUBLIC_SECTOR_EMPLOYEES = new SocialGroupKey("socialGroups.publicSectorEmployees");
    public static final SocialGroupKey RETIRED = new SocialGroupKey("socialGroups.retired");
    public static final SocialGroupKey UNEMPLOYED = new SocialGroupKey("socialGroups.unemployed");
    
    public static final PolicyKey[] getPolicyKeys() {
        return new PolicyKey[]{
            INCOME_TAX, VAT, UNEMPLOYMENT_BENEFITS, RETIREMENT_BENEFITS, PUBLIC_SECTOR_FUNDING
        };
    }
    
    public static final SocialGroupKey[] getSocialGroupKeys() {
        return new SocialGroupKey[]{
            PRIVATE_SECTOR_EMPLOYEES, PUBLIC_SECTOR_EMPLOYEES, RETIRED, UNEMPLOYED
        };
    }
    
    private Keys() {}
    
}
