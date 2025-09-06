package ru.minced.client.core.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.file.impl.DraggableFile;
import ru.minced.client.core.file.impl.FriendsFile;
import ru.minced.client.core.file.impl.GPSFile;
import ru.minced.client.core.file.impl.ModuleFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileRepository {
    List<ClientFile> clientFiles = new ArrayList<>();

    public void setup() {
        register(
                new ModuleFile(),
                new DraggableFile(),
                new FriendsFile(),
                new GPSFile()
        );
    }

    public void register(ClientFile... clientFIle) {
        clientFiles.addAll(List.of(clientFIle));
    }
}