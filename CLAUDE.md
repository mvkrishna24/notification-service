# Project Instructions

## Git Commit Rules
- Author: Martha Vamshi Krishna <marthvamshikrishna1024@gmail.com>
- Committer: Martha Vamshi Krishna <marthvamshikrishna1024@gmail.com>
- No Co-authored-by trailers of any kind
- No "Generated with Claude Code" footers
- No Claude / Anthropic / AI attribution anywhere in commit messages, bodies, or trailers
- Conventional Commits format only: `<type>(<scope>): <description>`
- No emojis in commit messages
- Imperative mood: "add" not "added"
- Subject line under 72 characters

## Code Conventions
- Constructor injection only — no field @Autowired
- DTOs as Java records where possible
- Lombok: @Slf4j, @RequiredArgsConstructor, @Getter — never @Data on entities
- Jakarta Validation on all external inputs
- Centralized exception handling via @RestControllerAdvice
- Never N+1 queries — always fetch what you need in one query
- Every external call (Redis, DB, Kafka) has a configured timeout

## Package: com.vamshi.notification
