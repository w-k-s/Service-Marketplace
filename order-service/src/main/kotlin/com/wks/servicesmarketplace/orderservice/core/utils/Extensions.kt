import java.sql.Timestamp
import java.time.*

fun OffsetDateTime.toUTCTimestamp()
= Timestamp.valueOf(LocalDateTime.ofInstant(this.toInstant(), ZoneOffset.UTC));

fun Timestamp.toUTCOffsetDateTime()
= OffsetDateTime.ofInstant(Instant.ofEpochMilli(this.time), ZoneId.of("UTC"))