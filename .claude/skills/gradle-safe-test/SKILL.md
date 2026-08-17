---
name: gradle-safe-test
description: Roda ./gradlew test/build neste repo de forma segura contra o hang conhecido do sandbox (conexão HTTPS morta pro Maven Central). Use antes de qualquer comando gradle no backend/.
---

Neste sandbox, `./gradlew` às vezes trava indefinidamente sem erro: a conexão HTTPS pro Maven
Central fica morta (`CloseWait`) sem o cliente Java perceber. Ver `CLAUDE.md` do repo.

## Procedimento

1. Sempre rode com `--no-daemon --console=plain` (sem daemon persistente = sem lock zumbi entre
   tentativas):
   ```bash
   cd backend && ./gradlew test --no-daemon --console=plain
   ```
2. Se travar (sem output novo por >2min): mate os processos `java` residentes do gradle e rode de
   novo no mesmo formato acima.
   ```bash
   # Windows
   taskkill /F /IM java.exe
   # Unix
   pkill -f gradle
   ```
3. Se depois de matar à força o build seguinte reportar tudo `up-to-date` de forma suspeita
   (cache pode ter corrompido pela morte abrupta), rode uma vez com `clean`:
   ```bash
   cd backend && ./gradlew clean test --no-daemon --console=plain
   ```

Nunca rode `./gradlew` sem `--no-daemon` neste projeto — é a causa raiz do lock zumbi.
