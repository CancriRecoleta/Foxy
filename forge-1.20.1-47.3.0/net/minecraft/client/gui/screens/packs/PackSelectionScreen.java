//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PackSelectionScreen extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int LIST_WIDTH = 200;
    private static final Component DRAG_AND_DROP;
    private static final Component DIRECTORY_BUTTON_TOOLTIP;
    private static final int RELOAD_COOLDOWN = 20;
    private static final ResourceLocation DEFAULT_ICON;
    private final PackSelectionModel model;
    @Nullable
    private Watcher watcher;
    private long ticksToReload;
    private TransferableSelectionList availablePackList;
    private TransferableSelectionList selectedPackList;
    private final Path packDir;
    private Button doneButton;
    private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

    public PackSelectionScreen(PackRepository p_275398_, Consumer<PackRepository> p_275659_, Path p_275522_, Component p_275337_) {
        super(p_275337_);
        this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, p_275398_, p_275659_);
        this.packDir = p_275522_;
        this.watcher = net.minecraft.client.gui.screens.packs.PackSelectionScreen.Watcher.create(p_275522_);
    }

    public void onClose() {
        this.model.commit();
        this.closeWatcher();
    }

    private void closeWatcher() {
        if (this.watcher != null) {
            try {
                this.watcher.close();
                this.watcher = null;
            } catch (Exception var2) {
            }
        }

    }

    protected void init() {
        this.availablePackList = new TransferableSelectionList(this.minecraft, this, 200, this.height, Component.translatable("pack.available.title"));
        this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
        this.addWidget(this.availablePackList);
        this.selectedPackList = new TransferableSelectionList(this.minecraft, this, 200, this.height, Component.translatable("pack.selected.title"));
        this.selectedPackList.setLeftPos(this.width / 2 + 4);
        this.addWidget(this.selectedPackList);
        this.addRenderableWidget(Button.builder(Component.translatable("pack.openFolder"), (p_100004_) -> {
            Util.getPlatform().openUri(this.packDir.toUri());
        }).bounds(this.width / 2 - 154, this.height - 48, 150, 20).tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP)).build());
        this.doneButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_100036_) -> {
            this.onClose();
        }).bounds(this.width / 2 + 4, this.height - 48, 150, 20).build());
        this.reload();
    }

    public void tick() {
        if (this.watcher != null) {
            try {
                if (this.watcher.pollForChanges()) {
                    this.ticksToReload = 20L;
                }
            } catch (IOException var2) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", this.packDir);
                this.closeWatcher();
            }
        }

        if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
            this.reload();
        }

    }

    private void populateLists() {
        this.updateList(this.selectedPackList, this.model.getSelected());
        this.updateList(this.availablePackList, this.model.getUnselected());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updateList(TransferableSelectionList p_100014_, Stream<PackSelectionModel.Entry> p_100015_) {
        p_100014_.children().clear();
        TransferableSelectionList.PackEntry transferableselectionlist$packentry = (TransferableSelectionList.PackEntry)p_100014_.getSelected();
        String s = transferableselectionlist$packentry == null ? "" : transferableselectionlist$packentry.getPackId();
        p_100014_.setSelected((TransferableSelectionList.PackEntry)null);
        p_100015_.filter(PackSelectionModel.Entry::notHidden).forEach((p_280875_) -> {
            TransferableSelectionList.PackEntry transferableselectionlist$packentry1 = new TransferableSelectionList.PackEntry(this.minecraft, p_100014_, p_280875_);
            p_100014_.children().add(transferableselectionlist$packentry1);
            if (p_280875_.getId().equals(s)) {
                p_100014_.setSelected(transferableselectionlist$packentry1);
            }

        });
    }

    public void updateFocus(TransferableSelectionList p_265419_) {
        TransferableSelectionList transferableselectionlist = this.selectedPackList == p_265419_ ? this.availablePackList : this.selectedPackList;
        this.changeFocus(ComponentPath.path((GuiEventListener)transferableselectionlist.getFirstElement(), (ContainerEventHandler[])(transferableselectionlist, this)));
    }

    public void clearSelected() {
        this.selectedPackList.setSelected((TransferableSelectionList.PackEntry)null);
        this.availablePackList.setSelected((TransferableSelectionList.PackEntry)null);
    }

    private void reload() {
        this.model.findNewPacks();
        this.populateLists();
        this.ticksToReload = 0L;
        this.packIcons.clear();
    }

    public void render(GuiGraphics p_281318_, int p_99996_, int p_99997_, float p_99998_) {
        this.renderDirtBackground(p_281318_);
        this.availablePackList.render(p_281318_, p_99996_, p_99997_, p_99998_);
        this.selectedPackList.render(p_281318_, p_99996_, p_99997_, p_99998_);
        p_281318_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 8, 16777215);
        p_281318_.drawCenteredString(this.font, (Component)DRAG_AND_DROP, this.width / 2, 20, 16777215);
        super.render(p_281318_, p_99996_, p_99997_, p_99998_);
    }

    protected static void copyPacks(Minecraft p_100000_, List<Path> p_100001_, Path p_100002_) {
        MutableBoolean mutableboolean = new MutableBoolean();
        p_100001_.forEach((p_170009_) -> {
            try {
                Stream<Path> stream = Files.walk(p_170009_);

                try {
                    stream.forEach((p_170005_) -> {
                        try {
                            Util.copyBetweenDirs(p_170009_.getParent(), p_100002_, p_170005_);
                        } catch (IOException var5) {
                            IOException ioexception1 = var5;
                            LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{p_170005_, p_100002_, ioexception1});
                            mutableboolean.setTrue();
                        }

                    });
                } catch (Throwable var7) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }

                    throw var7;
                }

                if (stream != null) {
                    stream.close();
                }
            } catch (IOException var8) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", p_170009_, p_100002_);
                mutableboolean.setTrue();
            }

        });
        if (mutableboolean.isTrue()) {
            SystemToast.onPackCopyFailure(p_100000_, p_100002_.toString());
        }

    }

    public void onFilesDrop(List<Path> p_100029_) {
        String s = (String)p_100029_.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
        this.minecraft.setScreen(new ConfirmScreen((p_280877_) -> {
            if (p_280877_) {
                copyPacks(this.minecraft, p_100029_, this.packDir);
                this.reload();
            }

            this.minecraft.setScreen(this);
        }, Component.translatable("pack.dropConfirm"), Component.literal(s)));
    }

    private ResourceLocation loadPackIcon(TextureManager p_100017_, Pack p_100018_) {
        try {
            PackResources packresources = p_100018_.open();

            ResourceLocation var16;
            label70: {
                ResourceLocation var9;
                try {
                    IoSupplier<InputStream> iosupplier = packresources.getRootResource("pack.png");
                    if (iosupplier == null) {
                        var16 = DEFAULT_ICON;
                        break label70;
                    }

                    String s = p_100018_.getId();
                    String var10003 = Util.sanitizeName(s, ResourceLocation::validPathChar);
                    ResourceLocation resourcelocation = new ResourceLocation("minecraft", "pack/" + var10003 + "/" + Hashing.sha1().hashUnencodedChars(s) + "/icon");
                    InputStream inputstream = (InputStream)iosupplier.get();

                    try {
                        NativeImage nativeimage = NativeImage.read(inputstream);
                        p_100017_.register((ResourceLocation)resourcelocation, (AbstractTexture)(new DynamicTexture(nativeimage)));
                        var9 = resourcelocation;
                    } catch (Throwable var12) {
                        if (inputstream != null) {
                            try {
                                inputstream.close();
                            } catch (Throwable var11) {
                                var12.addSuppressed(var11);
                            }
                        }

                        throw var12;
                    }

                    if (inputstream != null) {
                        inputstream.close();
                    }
                } catch (Throwable var13) {
                    if (packresources != null) {
                        try {
                            packresources.close();
                        } catch (Throwable var10) {
                            var13.addSuppressed(var10);
                        }
                    }

                    throw var13;
                }

                if (packresources != null) {
                    packresources.close();
                }

                return var9;
            }

            if (packresources != null) {
                packresources.close();
            }

            return var16;
        } catch (Exception var14) {
            Exception exception = var14;
            LOGGER.warn("Failed to load icon from pack {}", p_100018_.getId(), exception);
            return DEFAULT_ICON;
        }
    }

    private ResourceLocation getPackIcon(Pack p_99990_) {
        return (ResourceLocation)this.packIcons.computeIfAbsent(p_99990_.getId(), (p_280879_) -> {
            return this.loadPackIcon(this.minecraft.getTextureManager(), p_99990_);
        });
    }

    static {
        DRAG_AND_DROP = Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY);
        DIRECTORY_BUTTON_TOOLTIP = Component.translatable("pack.folderInfo");
        DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
    }

    @OnlyIn(Dist.CLIENT)
    static class Watcher implements AutoCloseable {
        private final WatchService watcher;
        private final Path packPath;

        public Watcher(Path p_250327_) throws IOException {
            this.packPath = p_250327_;
            this.watcher = p_250327_.getFileSystem().newWatchService();

            try {
                this.watchDir(p_250327_);
                DirectoryStream<Path> directorystream = Files.newDirectoryStream(p_250327_);

                try {
                    Iterator var3 = directorystream.iterator();

                    while(var3.hasNext()) {
                        Path path = (Path)var3.next();
                        if (Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                            this.watchDir(path);
                        }
                    }
                } catch (Throwable var6) {
                    if (directorystream != null) {
                        try {
                            directorystream.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }

                    throw var6;
                }

                if (directorystream != null) {
                    directorystream.close();
                }

            } catch (Exception var7) {
                Exception exception = var7;
                this.watcher.close();
                throw exception;
            }
        }

        @Nullable
        public static Watcher create(Path p_252119_) {
            try {
                return new Watcher(p_252119_);
            } catch (IOException var2) {
                IOException ioexception = var2;
                PackSelectionScreen.LOGGER.warn("Failed to initialize pack directory {} monitoring", p_252119_, ioexception);
                return null;
            }
        }

        private void watchDir(Path p_100050_) throws IOException {
            p_100050_.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean pollForChanges() throws IOException {
            boolean flag = false;

            WatchKey watchkey;
            while((watchkey = this.watcher.poll()) != null) {
                Iterator var3 = watchkey.pollEvents().iterator();

                while(var3.hasNext()) {
                    WatchEvent<?> watchevent = (WatchEvent)var3.next();
                    flag = true;
                    if (watchkey.watchable() == this.packPath && watchevent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path path = this.packPath.resolve((Path)watchevent.context());
                        if (Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                            this.watchDir(path);
                        }
                    }
                }

                watchkey.reset();
            }

            return flag;
        }

        public void close() throws IOException {
            this.watcher.close();
        }
    }
}
