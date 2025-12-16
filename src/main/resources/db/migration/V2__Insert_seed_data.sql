-- V2__Insert_seed_data.sql
-- Seed data for reference data

-- Insert common customer labels
INSERT INTO customers (wechat_openid, name, phone, source, distance, first_visit_at, label, created_at, updated_at) VALUES
('wx_openid_001', '张三', '13800138001', '微信扫码', 0.5, '2024-01-15 10:30:00', '新用户;微信扫码;附近用户', '2024-01-15 10:30:00', '2024-01-15 10:30:00'),
('wx_openid_002', '李四', '13800138002', '朋友圈广告', 2.3, '2024-01-16 14:20:00', '营销渠道;高价值用户', '2024-01-16 14:20:00', '2024-01-16 14:20:00'),
('wx_openid_003', '王五', '13800138003', '朋友推荐', 1.8, '2024-01-17 09:15:00', '推荐用户;活跃用户', '2024-01-17 09:15:00', '2024-01-17 09:15:00');

-- Insert recharge promotions
INSERT INTO recharge_promotions (name, description, recharge_amount, bonus_amount, is_active, valid_from, valid_to, max_usage_per_user, total_usage_limit, current_usage, created_at, updated_at) VALUES
('新用户充值优惠', '新用户首次充值享受额外奖励', 100.00, 20.00, TRUE, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, 1000, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('充值返现活动', '充值满200元返现50元', 200.00, 50.00, TRUE, '2024-01-01 00:00:00', '2024-06-30 23:59:59', 2, 500, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('限时充值优惠', '周末充值享受额外奖励', 150.00, 30.00, TRUE, '2024-01-01 00:00:00', '2024-03-31 23:59:59', 1, 200, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- Insert discount campaigns
INSERT INTO discount_campaigns (name, description, discount_type, discount_value, min_order_amount, max_discount_amount, is_active, valid_from, valid_to, max_usage_per_user, total_usage_limit, current_usage, created_at, updated_at) VALUES
('全场8折优惠', '全场商品享受8折优惠', 'PERCENTAGE', 0.80, 50.00, 100.00, TRUE, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 3, 1000, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('满减优惠', '满100减20优惠活动', 'FIXED_AMOUNT', 20.00, 100.00, NULL, TRUE, '2024-01-01 00:00:00', '2024-06-30 23:59:59', 2, 500, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('新用户专享', '新用户专享满50减10', 'FIXED_AMOUNT', 10.00, 50.00, NULL, TRUE, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, 2000, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- Insert pond articles
INSERT INTO pond_articles (title, content, author, category, tags, view_count, is_published, published_at, featured_image_url, reading_time_minutes, created_at, updated_at) VALUES
('如何使用余额充值', '详细介绍余额充值的步骤和注意事项...', '管理员', '使用指南', '充值,余额,使用指南', 1250, TRUE, '2024-01-10 10:00:00', 'https://example.com/images/recharge-guide.jpg', 5, '2024-01-10 10:00:00', '2024-01-10 10:00:00'),
('会员订阅服务介绍', '全面介绍会员订阅服务的功能和优势...', '管理员', '服务介绍', '会员,订阅,服务', 890, TRUE, '2024-01-12 14:30:00', 'https://example.com/images/membership.jpg', 8, '2024-01-12 14:30:00', '2024-01-12 14:30:00'),
('优惠活动攻略', '如何最大化利用各种优惠活动...', '管理员', '优惠攻略', '优惠,活动,攻略', 567, TRUE, '2024-01-14 16:20:00', 'https://example.com/images/promotions.jpg', 6, '2024-01-14 16:20:00', '2024-01-14 16:20:00'),
('常见问题解答', '用户最关心的问题和详细解答...', '管理员', '帮助中心', 'FAQ,帮助,问题', 2100, TRUE, '2024-01-08 09:00:00', 'https://example.com/images/faq.jpg', 10, '2024-01-08 09:00:00', '2024-01-08 09:00:00'),
('产品更新日志', '最新功能更新和改进内容...', '管理员', '产品动态', '更新,日志,新功能', 445, TRUE, '2024-01-16 11:45:00', 'https://example.com/images/changelog.jpg', 4, '2024-01-16 11:45:00', '2024-01-16 11:45:00');

-- Create accounts for customers
INSERT INTO accounts (customer_id, balance, total_recharge, total_spend, created_at, updated_at) VALUES
(1, 150.50, 200.00, 49.50, '2024-01-15 10:30:00', '2024-01-15 10:30:00'),
(2, 75.25, 100.00, 24.75, '2024-01-16 14:20:00', '2024-01-16 14:20:00'),
(3, 300.00, 350.00, 50.00, '2024-01-17 09:15:00', '2024-01-17 09:15:00');

-- Insert transactions
INSERT INTO transactions (customer_id, account_id, type, amount, project_type, metadata, created_at, updated_at) VALUES
(1, 1, 'RECHARGE', 200.00, 'GENERAL', '{"promotion_id": 1, "bonus_amount": 20.00}', '2024-01-15 10:35:00', '2024-01-15 10:35:00'),
(1, 1, 'SPEND', 49.50, 'POND_ARTICLES', '{"article_id": 1, "subscription_type": "premium"}', '2024-01-16 15:20:00', '2024-01-16 15:20:00'),
(2, 2, 'RECHARGE', 100.00, 'PROMOTION', '{"campaign_id": 2}', '2024-01-16 14:25:00', '2024-01-16 14:25:00'),
(2, 2, 'SPEND', 24.75, 'SUBSCRIPTION', '{"subscription_type": "monthly"}', '2024-01-17 10:30:00', '2024-01-17 10:30:00'),
(3, 3, 'RECHARGE', 350.00, 'GENERAL', '{"payment_method": "wechat"}', '2024-01-17 09:20:00', '2024-01-17 09:20:00'),
(3, 3, 'SPEND', 50.00, 'DISCOUNT_CAMPAIGN', '{"discount_id": 2, "original_amount": 70.00}', '2024-01-18 14:15:00', '2024-01-18 14:15:00');

-- Insert article subscriptions
INSERT INTO article_subscriptions (customer_id, article_id, is_active, subscription_type, expires_at, created_at, updated_at) VALUES
(1, 1, TRUE, 'PREMIUM', '2024-02-15 10:35:00', '2024-01-16 15:20:00', '2024-01-16 15:20:00'),
(2, 2, TRUE, 'MONTHLY', '2024-02-16 14:25:00', '2024-01-17 10:30:00', '2024-01-17 10:30:00'),
(3, 3, TRUE, 'FREE', NULL, '2024-01-18 14:15:00', '2024-01-18 14:15:00');

-- Insert comments
INSERT INTO comments (customer_id, article_id, content, parent_id, is_approved, like_count, created_at, updated_at) VALUES
(1, 1, '这篇文章写得很详细，帮助我快速了解了充值流程！', NULL, TRUE, 5, '2024-01-16 16:30:00', '2024-01-16 16:30:00'),
(2, 1, '谢谢分享，确实很实用', 1, TRUE, 2, '2024-01-16 17:15:00', '2024-01-16 17:15:00'),
(3, 2, '会员服务确实不错，推荐大家订阅', NULL, TRUE, 8, '2024-01-17 11:20:00', '2024-01-17 11:20:00'),
(1, 4, 'FAQ很全面，解决了我大部分疑问', NULL, TRUE, 3, '2024-01-18 09:45:00', '2024-01-18 09:45:00');

-- Insert admin users
INSERT INTO admin_users (username, email, password, full_name, role, is_active, login_count, created_at, updated_at) VALUES
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAqn8VqFGEW.dpmo2kO..NNECa7G', '系统管理员', 'ADMIN', TRUE, 5, '2024-01-01 00:00:00', '2024-01-15 14:30:00'),
('moderator', 'moderator@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAqn8VqFGEW.dpmo2kO..NNECa7G', '内容管理员', 'MODERATOR', TRUE, 12, '2024-01-01 00:00:00', '2024-01-16 10:15:00'),
('support', 'support@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAqn8VqFGEW.dpmo2kO..NNECa7G', '客服专员', 'SUPPORT', TRUE, 28, '2024-01-01 00:00:00', '2024-01-18 16:45:00');