# Spring AI Structured Output POC

**LLMs return text. Services need types.** This POC turns a plain-English order question into a validated Java `record` with Spring AI, and shows what happens when the model gets it wrong.

## Stack
- Java 21, Spring Boot 3.5, Spring AI 1.0 (Anthropic Claude starter)
- Bean Validation on the record + a fallback path
- Temperature 0

## How it works
```java
OrderQuery q = chatClient.prompt()
        .system("...rules: use null when not mentioned, never invent a name...")
        .user(question)
        .call()
        .entity(OrderQuery.class);   // JSON schema from the record, mapped back automatically
```
Every result then goes through Bean Validation:
- **OK**: parsed and valid
- **INVALID**: parsed, but a field breaks the contract (e.g. order ID not `ORD-12345`)
- **FALLBACK**: the model's output could not be used at all

## Run it
```bash
export ANTHROPIC_API_KEY=your-key
export ANTHROPIC_MODEL=a-current-claude-model-name
mvn spring-boot:run
```
Windows PowerShell:
```powershell
$env:ANTHROPIC_API_KEY="your-key"
$env:ANTHROPIC_MODEL="a-current-claude-model-name"
mvn spring-boot:run
```

## Try it
- One question: `http://localhost:8080/parse?q=Status of order ORD-48213 for Priya`
- All 5 test questions: `http://localhost:8080/run-tests`

| # | Question | Expected |
|---|---|---|
| 1 | Show me John Smith's shipped orders from last week | OK, dates filled |
| 2 | Status of order ORD-48213 for Priya | OK, orderId set |
| 3 | Cancelled orders for Mike in October | OK, October range |
| 4 | Order 4821 for Sara | INVALID: bad ID format |
| 5 | What's the weather today? | INVALID or FALLBACK |

## Lessons
- Structured output is a contract, not a guarantee. Validate every field you depend on.
- Put "never invent" rules in the system prompt.
- Temperature 0 for extraction.
- Always design the failure path.
