package com.github.pixelrunstudios.ChemHelper;

import java.util.Map;

public class Stoichiometry{

	public static double convert(String inEq, String outEq, double amt,
			String inUnit, String inSub, String outUnit, String outSub,
			Map<String, String> data){
		Pair<ChemistryUnit, ChemistryUnit> iao = ChemistryParser.
				parseEquation(inEq, outEq);
		if(iao == null){
			Debug.printlnDeep("N/A");
			throw new IllegalArgumentException("Cannot be converted!");
		}
		ChemistryUnit in = iao.getValueOne();
		ChemistryUnit out = iao.getValueTwo();
		ChemistryUnit inZ = ChemistryParser.parseCompound(inSub);
		ChemistryUnit outZ = ChemistryParser.parseCompound(outSub);
		int inNum = 0;
		boolean inSet = false;
		int outNum = 0;
		boolean outSet = false;
		Pair<Boolean, Integer> inp = huntDown(in, inZ, outZ);
		if(inp.getValueTwo() < 0){
			Debug.printlnDeep("N/A");
			throw new IllegalArgumentException("Cannot be converted!");
		}
		else{
			if(inp.getValueOne()){
				inNum = inp.getValueTwo();
				inSet = true;
			}
			else{
				outNum = inp.getValueTwo();
				outSet = true;
			}
		}
		Pair<Boolean, Integer> oup = huntDown(out, inZ, outZ);
		if(oup.getValueTwo() < 0){
			Debug.printlnDeep("N/A");
			return 0;
		}
		else{
			if(oup.getValueOne()){
				if(inSet){
					Pair<Boolean, Integer> inZero = huntDown(out, null, outZ);
					if(inZero.getValueTwo() < 0){
						Debug.printlnDeep("N/A");
						return 0;
					}
					else{
						outNum = inZero.getValueTwo();
					}
				}
				else{
					inNum = oup.getValueTwo();
				}
			}
			else{
				if(outSet){
					Debug.printlnDeep("N/A");
					return 0;
				}
				else{
					outNum = oup.getValueTwo();
				}
			}
		}
		double outOverIn = (double) outNum / (double) inNum;
		double inToMole = 0;
		switch(inUnit){
			case "g":
				inToMole = 1 / MolarMass.calculate(inZ, data);
				break;
			case "mol":
				inToMole = 1;
				break;
			default:
				Debug.printlnDeep("N/A");
				throw new IllegalArgumentException("Cannot be converted!");
		}
		double outToMole = 0;
		switch(outUnit){
			case "g":
				outToMole = MolarMass.calculate(outZ, data);
				break;
			case "mol":
				outToMole = 1;
				break;
			default:
				Debug.printlnDeep("N/A");
				throw new IllegalArgumentException("Cannot be converted!");
		}
		return amt * inToMole * outOverIn * outToMole;
	}

	//True if inExpression is found
	private static Pair<Boolean, Integer> huntDown(ChemistryUnit in, ChemistryUnit inExpression,
			ChemistryUnit outExpression){
		if(inExpression != null && in.equals(inExpression)){
			return Pair.make(true, 1);
		}
		else if(outExpression != null && in.equals(outExpression)){
			return Pair.make(false, 1);
		}
		else if(in.getType() == ChemistryUnit.TYPE_BASE){
			return Pair.make(false, -1);
		}
		for(Map.Entry<ChemistryUnit, Integer> cu : in.getSubUnits().entrySet()){
			Pair<Boolean, Integer> pair = huntDown(cu.getKey(), inExpression, outExpression);
			if(pair.getValueTwo().equals(-1)){
				continue;
			}
			else{
				return Pair.make(pair.getValueOne(), pair.getValueTwo() * cu.getValue());
			}
		}
		return Pair.make(false, -1);
	}

}
