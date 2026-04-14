CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    bio TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS follows (
    id BIGSERIAL PRIMARY KEY,
    user_following BIGINT NOT NULL,
    user_followed BIGINT NOT NULL,
    CONSTRAINT fk_follows_users_following FOREIGN KEY (user_following) REFERENCES users (id),
    CONSTRAINT fk_follows_users_followed FOREIGN KEY (user_followed) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    post_content TEXT NOT NULL,
    post_user_id BIGINT NOT NULL,
    CONSTRAINT fk_posts_users FOREIGN KEY (post_user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    CONSTRAINT fk_likes_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_likes_posts FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE IF NOT EXISTS replies (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_replies_posts FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_replies_users FOREIGN KEY (user_id) REFERENCES users (id)
);

INSERT INTO users (username, email, password, bio, is_private, role)
VALUES
    ('alice', 'alice@blodged.dev', 'password123', 'Coffee-fueled full-stack tinkerer.', FALSE, 'USER'),
    ('mike', 'mike@blodged.dev', 'password123', 'Runner, reader, and weekend photographer.', FALSE, 'USER'),
    ('sophia', 'sophia@blodged.dev', 'password123', 'Frontend dev who loves clean UI.', FALSE, 'USER'),
    ('jordan', 'jordan@blodged.dev', 'password123', 'Backend engineer exploring Rust on the side.', TRUE, 'USER'),
    ('admin', 'admin@blodged.dev', 'adminpass123', 'Blodged platform administrator.', FALSE, 'ADMIN')
ON CONFLICT (username) DO NOTHING;

INSERT INTO posts (post_content, post_user_id)
VALUES
    ('Just shipped my first feature this week. Tiny win, big motivation.', (SELECT id FROM users WHERE username = 'alice')),
    ('Tried a new pour-over recipe this morning. Highly recommend.', (SELECT id FROM users WHERE username = 'alice')),
    ('Ran 5 miles before sunrise and somehow still made standup.', (SELECT id FROM users WHERE username = 'mike')),
    ('Does anyone have favorite camera settings for cloudy afternoons?', (SELECT id FROM users WHERE username = 'mike')),
    ('Refactored a component from 300 lines to 120. Feels great.', (SELECT id FROM users WHERE username = 'sophia')),
    ('Working on a small design system for side projects.', (SELECT id FROM users WHERE username = 'sophia')),
    ('Swapped in Redis caching today and cut API latency significantly.', (SELECT id FROM users WHERE username = 'jordan')),
    ('Thinking about writing a post on database indexing basics.', (SELECT id FROM users WHERE username = 'jordan')),
    ('Welcome to Blodged! This environment includes mock data for testing.', (SELECT id FROM users WHERE username = 'admin'))
ON CONFLICT DO NOTHING;
