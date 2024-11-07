package limechain.etherium.fetcher.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import limechain.etherium.fetcher.db.model.EthereumTransaction;

public interface AccountOldRepository extends JpaRepository<EthereumTransaction, Long> {

}