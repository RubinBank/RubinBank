package me.criztovyl.rubinbank.bankomat;

import java.util.HashMap;

public enum BankomatArgument {
    /**
     * X-Location
     */
    LOCX,
    /**
     * Y-Location
     */
    LOCY,
    /**
     * Z-Location
     */
    LOCZ,
    /**
     * World UUID
     */
    LOCWORLD,
    /**
     * the place of the Bankomat
     */
    PLACE,
    /**
     * the type
     */
    TYPE,
    /**
     * the trigger position
     */
    POS;
    /**
     * @param args a List of Arguments
     * @return the Argument of this type
     */
    public String getArg(HashMap<String, String> args){
        switch(this){
        case LOCWORLD:
                return args.get("LOCWORLD");
        case LOCX:
                return args.get("LOCX");
        case LOCY:
                return args.get("LOCY");
        case LOCZ:
                return args.get("LOCZ");
        case PLACE:
                return args.get("PLACE");
        case POS:
                return args.get("POS");
        case TYPE:
                return args.get("TYPE");
        }
        return null;
}
    /**
     * Stores a Argument to a List
     * @param args the List
     * @param arg the Argument
     * @return the List with the Argument
     */
    public HashMap<String, String> saveArg(HashMap<String, String> args, String arg){
        switch(this){
        case LOCWORLD:
                args.put("LOCWORLD", arg);
                return args;
        case LOCX:
                args.put("LOCX", arg);
                return args;
        case LOCY:
                args.put("LOCY", arg);
                return args;
        case LOCZ:
                args.put("LOCZ", arg);
                return args;
        case PLACE:
                args.put("PLACE", arg);
                return args;
        case POS:
                args.put("POS", arg);
                return args;
        case TYPE:
                args.put("TYPE", arg);
                return args;
        }
        return args;
}
}
