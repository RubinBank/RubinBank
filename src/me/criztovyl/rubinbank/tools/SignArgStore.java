package me.criztovyl.rubinbank.tools;

import java.util.HashMap;

import me.criztovyl.clickless.tools.Store;

public class SignArgStore implements Store<SignArg>{
	private HashMap<SignArg, String> args;
	public SignArgStore(){
		args = new HashMap<SignArg, String>();
	}
	@Override
	public HashMap<SignArg, String> fetch() {
		return args;
	}

	@Override
	public void store(HashMap<SignArg, String> arg0) {
		args = arg0;
	}
	@Override
	public void store(SignArg arg0, String arg1) {
		args.put(arg0, arg1);
	}
	@Override
	public String fetch(SignArg arg0) {
		return args.get(arg0);
	}

}
