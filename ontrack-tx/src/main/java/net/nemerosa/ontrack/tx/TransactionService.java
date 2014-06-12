package net.nemerosa.ontrack.tx;

public interface TransactionService {

	Transaction start();

	Transaction start(boolean nested);

	Transaction get();

}
