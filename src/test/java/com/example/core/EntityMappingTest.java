package com.example.core;

import com.example.core.entity.*;
import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;
import com.example.core.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entity mapping validation test
 */
@SpringBootTest
@Transactional
public class EntityMappingTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Test
    public void testCustomerCreation() {
        Customer customer = new Customer();
        customer.setWechatOpenid("test_openid_123");
        customer.setName("Test User");
        customer.setPhone("13900139001");
        customer.setSource("test");
        customer.setFirstVisitAt(LocalDateTime.now());
        customer.setLabel("test_user");

        Customer saved = customerRepository.save(customer);
        assertNotNull(saved.getId());
        assertEquals("test_openid_123", saved.getWechatOpenid());
    }

    @Test
    public void testAccountCreation() {
        Customer customer = new Customer();
        customer.setWechatOpenid("test_openid_456");
        customer.setName("Account Test User");
        customer.setSource("test");
        customer.setFirstVisitAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        Account account = new Account(customer);
        account.setBalance(BigDecimal.valueOf(100.50));
        account.setTotalRecharge(BigDecimal.valueOf(100.50));
        account.setTotalSpend(BigDecimal.ZERO);

        Account saved = accountRepository.save(account);
        assertNotNull(saved.getId());
        assertEquals(customer.getId(), saved.getCustomer().getId());
        assertEquals(BigDecimal.valueOf(100.50), saved.getBalance());
    }

    @Test
    public void testTransactionCreation() {
        Customer customer = new Customer();
        customer.setWechatOpenid("test_openid_789");
        customer.setName("Transaction Test User");
        customer.setSource("test");
        customer.setFirstVisitAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        Account account = new Account(customer);
        account = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAccount(account);
        transaction.setType(TransactionType.RECHARGE);
        transaction.setAmount(BigDecimal.valueOf(50.00));
        transaction.setProjectType(ProjectType.GENERAL);
        transaction.setMetadata("{\"test\": true}");

        Transaction saved = transactionRepository.save(transaction);
        assertNotNull(saved.getId());
        assertEquals(TransactionType.RECHARGE, saved.getType());
        assertEquals(BigDecimal.valueOf(50.00), saved.getAmount());
    }

    @Test
    public void testCustomerRepositoryQueries() {
        Customer customer1 = new Customer();
        customer1.setWechatOpenid("test_openid_001");
        customer1.setName("Query Test User 1");
        customer1.setSource("微信扫码");
        customer1.setFirstVisitAt(LocalDateTime.now().minusDays(5));
        customer1.setLabel("test;微信扫码;活跃");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setWechatOpenid("test_openid_002");
        customer2.setName("Query Test User 2");
        customer2.setSource("朋友圈广告");
        customer2.setFirstVisitAt(LocalDateTime.now().minusDays(2));
        customer2.setLabel("test;朋友圈广告;高价值");
        customerRepository.save(customer2);

        // Test find by source
        List<Customer> wechatUsers = customerRepository.findBySource("微信扫码");
        assertEquals(1, wechatUsers.size());
        assertEquals("Query Test User 1", wechatUsers.get(0).getName());

        // Test find by label containing
        List<Customer> labeledUsers = customerRepository.findByLabelContaining("微信扫码");
        assertEquals(1, labeledUsers.size());
    }

    @Test
    public void testTransactionRepositoryQueries() {
        Customer customer = new Customer();
        customer.setWechatOpenid("test_openid_repo");
        customer.setName("Repository Test User");
        customer.setSource("test");
        customer.setFirstVisitAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        Account account = new Account(customer);
        account = accountRepository.save(account);

        // Create multiple transactions
        Transaction recharge = new Transaction();
        recharge.setCustomer(customer);
        recharge.setAccount(account);
        recharge.setType(TransactionType.RECHARGE);
        recharge.setAmount(BigDecimal.valueOf(100.00));
        recharge.setProjectType(ProjectType.GENERAL);
        transactionRepository.save(recharge);

        Transaction spend = new Transaction();
        spend.setCustomer(customer);
        spend.setAccount(account);
        spend.setType(TransactionType.SPEND);
        spend.setAmount(BigDecimal.valueOf(30.00));
        spend.setProjectType(ProjectType.POND_ARTICLES);
        transactionRepository.save(spend);

        // Test find by customer
        List<Transaction> customerTransactions = transactionRepository.findByCustomerId(customer.getId());
        assertEquals(2, customerTransactions.size());

        // Test find by type
        List<Transaction> recharges = transactionRepository.findByType(TransactionType.RECHARGE);
        assertEquals(1, recharges.size());
        assertEquals(BigDecimal.valueOf(100.00), recharges.get(0).getAmount());

        // Test total amount calculation
        BigDecimal totalRecharge = transactionRepository.getTotalAmountByCustomerAndType(customer.getId(), TransactionType.RECHARGE);
        assertEquals(BigDecimal.valueOf(100.00), totalRecharge);
    }

    @Test
    public void testAdminUserCreation() {
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("testadmin");
        adminUser.setEmail("testadmin@example.com");
        adminUser.setPassword("encoded_password_hash");
        adminUser.setFullName("Test Administrator");
        adminUser.setRole("ADMIN");

        AdminUser saved = adminUserRepository.save(adminUser);
        assertNotNull(saved.getId());
        assertEquals("testadmin", saved.getUsername());
        assertEquals("ADMIN", saved.getRole());
    }

    @Test
    public void testEntityRelationships() {
        // Create a complete customer with account and transaction
        Customer customer = new Customer();
        customer.setWechatOpenid("test_rel_001");
        customer.setName("Relationship Test User");
        customer.setSource("test");
        customer.setFirstVisitAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        Account account = new Account(customer);
        account = accountRepository.save(account);

        // Set bidirectional relationships
        customer.setAccount(account);
        account.setCustomer(customer);
        customerRepository.save(customer);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAccount(account);
        transaction.setType(TransactionType.RECHARGE);
        transaction.setAmount(BigDecimal.valueOf(200.00));
        transaction.setProjectType(ProjectType.GENERAL);
        transaction = transactionRepository.save(transaction);

        // Update relationships
        account.getTransactions().add(transaction);
        customer.getTransactions().add(transaction);
        
        // Test relationship integrity
        assertEquals(customer.getId(), transaction.getCustomer().getId());
        assertEquals(account.getId(), transaction.getAccount().getId());
        assertEquals(1, customer.getTransactions().size());
        assertEquals(customer.getId(), customer.getAccount().getCustomer().getId());
    }
}