package net.lax1dude.eaglercraft.v1_8.internal.teavm.plugins;

import org.teavm.backend.javascript.TeaVMJavaScriptHost;
import org.teavm.backend.javascript.codegen.SourceWriter;
import org.teavm.backend.javascript.rendering.RenderingManager;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.spi.RendererListener;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

/**
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class TeaVMBrowserShimPlugin implements TeaVMPlugin {

	@Override
	public void install(TeaVMHost host) {
		TeaVMJavaScriptHost jsHost = host.getExtension(TeaVMJavaScriptHost.class);
		if(jsHost != null) {
			jsHost.add(new MissingClassShimRendererListener());
		}
	}

	private static class MissingClassShimRendererListener implements RendererListener {

		private static final String[] SHIMS = new String[] {
			"jn_Proxy",
			"jnfs_FileSystemProvider",
			"jnf_FileStore",
			"jnf_FileVisitor",
			"jnf_FileVisitResult",
			"jnfa_BasicFileAttributes",
			"jnfa_FileAttribute",
			"jnfa_FileAttributeView",
			"jnfa_FileStoreAttributeView",
			"jnfa_FileTime",
			"jnfa_UserPrincipalLookupService",
			"jnf_AccessMode",
			"jnf_DirectoryStream",
			"jnf_DirectoryStream$Filter",
			"jnf_FileSystem",
			"jnf_FileSystemNotFoundException",
			"jnf_FileVisitOption",
			"jnf_InvalidPathException",
			"jnf_NoSuchFileException",
			"jnf_OpenOption",
			"jnf_PathMatcher",
			"jnf_ReadOnlyFileSystemException",
			"jnf_WatchEvent$Kind",
			"jnf_WatchEvent$Modifier",
			"jnf_WatchKey",
			"jnf_WatchService",
			"jn_MappedByteBuffer",
			"jn_PortUnreachableException",
			"jn_Socket",
			"jn_SocketException",
			"jnc_AsynchronousFileChannel",
			"jnc_FileChannel",
			"jnc_FileChannel$MapMode",
			"jnc_GatheringByteChannel",
			"jnc_ScatteringByteChannel",
			"jnc_Selector",
			"ji_ObjectInputStream",
			"ji_ObjectOutputStream",
			"jl_Module",
			"jl_Process",
			"jl_Thread$State",
			"jlm_ThreadInfo",
			"js_AlgorithmParameters",
			"js_Date",
			"js_PermissionCollection",
			"js_ProtectionDomain",
			"js_Provider",
			"js_Provider$Service",
			"js_SecureRandom",
			"js_SignatureException",
			"js_Time",
			"js_Timestamp",
			"jsc_Certificate",
			"juca_AtomicIntegerArray",
			"juca_AtomicLongArray",
			"juca_AtomicReferenceArray",
			"jucl_LockSupport",
			"juc_CompletionStage",
			"juc_CompletableFuture",
			"juc_CompletionException",
			"juc_CopyOnWriteArrayList",
			"juc_CopyOnWriteArraySet",
			"juc_CountDownLatch",
			"juc_ForkJoinPool",
			"juc_ForkJoinPool$ForkJoinWorkerThreadFactory",
			"juc_RejectedExecutionException",
			"juc_ScheduledFuture",
			"juc_Semaphore",
			"juc_SynchronousQueue",
			"jlr_Cleaner",
			"jlr_Cleaner$",
			"jlr_Cleaner$Cleanable",
			"jlr_Executable",
			"jlr_GenericArrayType",
			"jlr_GenericDeclaration",
			"jlr_PhantomReference",
			"jlr_Proxy",
			"jlr_ReferenceQueue$",
			"jlr_TypeVariable",
			"jlr_UndeclaredThrowableException",
			"jlr_WildcardType",
			"jss_AlgorithmParameterSpec",
			"ju_Spliterators$AbstractSpliterator",
			"juc_ScheduledExecutorService",
			"inuc_GenericFutureListener",
			"inb_Bootstrap",
			"inb_ByteBuf",
			"inb_ByteBufAllocator",
			"inb_ServerBootstrap",
			"inb_Unpooled",
			"inc_Channel",
			"inc_ChannelHandlerContext",
			"inc_ChannelInitializer",
			"inc_DefaultEventLoopGroup",
			"inc_SimpleChannelInboundHandler",
			"ince_Epoll",
			"ince_EpollEventLoopGroup",
			"ince_EpollServerSocketChannel",
			"ince_EpollSocketChannel",
			"incl_LocalChannel",
			"incl_LocalServerChannel",
			"incn_NioEventLoopGroup",
			"incsn_NioServerSocketChannel",
			"incsn_NioSocketChannel",
			"inhc_EncoderException",
			"inu_AttributeKey",
			"inu_ByteProcessor",
			"inu_ReferenceCounted",
			"inu_ResourceLeakDetector",
			"inu_ResourceLeakDetector$",
			"inu_ResourceLeakDetector$Level",
			"inuc_Future",
			"inui_ThreadLocalRandom",
			"iudf_Hash$",
			"iudfc_Char2ObjectMap",
			"iudfc_Char2ObjectOpenHashMap",
			"iudfc_CharArrayList",
			"iudfc_CharArraySet",
			"iudfc_CharList",
			"iudfc_CharSet",
			"iudfi_Int2IntMap$",
			"iudfi_Int2ObjectMap$",
			"iudfi_Int2ObjectSortedMap$",
			"iudfi_Int2ShortMap",
			"iudfi_Int2ShortOpenHashMap",
			"iudfl_Long2ByteMap$",
			"iudfl_Long2FloatMap$",
			"iudfl_Long2FloatSortedMap$",
			"iudfl_Long2IntMap$",
			"iudfl_Long2IntSortedMap$",
			"iudfl_Long2LongMap$",
			"iudfl_Long2ObjectMap$",
			"iudfl_Long2ObjectSortedMap$",
			"iudfo_Object2BooleanMap$",
			"iudfo_Object2ByteMap$",
			"iudfo_Object2ByteSortedMap$",
			"iudfo_Object2FloatMap$",
			"iudfo_Object2IntMap$",
			"iudfo_Object2LongMap$",
			"iudfo_Object2ObjectMap$",
			"iudfo_Object2ObjectSortedMap$",
			"iudfo_ObjectAVLTreeSet",
			"iudfs_Short2BooleanMap$",
			"iudfs_Short2ObjectMap$",
			"ji_ObjectStreamClass",
			"jnc_FileChannel$",
			"jnf_ClosedFileSystemException",
			"jnf_DirectoryStream$",
			"jnf_FileSystems",
			"jnf_Paths",
			"jnf_ProviderMismatchException",
			"jnf_WatchEvent$",
			"jnfa_DosFileAttributeView",
			"js_Provider$",
			"js_Signature",
			"ju_MapEntry$",
			"ju_WeakHashMap",
			"juc_ConcurrentNavigableMap",
			"juc_ConcurrentSkipListMap",
			"juc_LinkedBlockingQueue$",
			"juc_LinkedBlockingQueue$1"
		};

		private static final String[] NOCLASSDEF_CLINIT_STUBS = new String[] {
			"cgcb_CharMatcher$BreakingWhitespace__clinit_",
			"cgcb_Functions$IdentityFunction__clinit_",
			"cgcb_Throwables__clinit_",
			"cgcc_ImmutableMap__clinit_",
			"cgcc_LocalCache$Strength__clinit_",
			"cgch_Hashing__clinit_",
			"cgci_Closeables__clinit_",
			"cgci_Closer$LoggingSuppressor__clinit_",
			"cgcr_Types$JavaVersion__clinit_",
			"cgcr_TypeToken$TypeFilter__clinit_",
			"cgcuc_AbstractFuture$Listener__clinit_",
			"cgcuc_AbstractFuture$UnsafeAtomicHelper__clinit_",
			"cgg_JsonNull__clinit_",
			"cjj_CRC32__clinit_",
			"cmbs_Suggestions__clinit_",
			"cmd_FunctionType$Instance$Mu__clinit_",
			"cmdf_PointFreeRule$SortInj__clinit_",
			"iudfb_BooleanArrays__clinit_",
			"jij_SystemImage__clinit_",
			"jir_Reflection__clinit_",
			"jm_Conversion__clinit_",
			"jnci_UTF8Charset__clinit_",
			"jnf_LinkOption__clinit_",
			"jtf_FormatStyle__clinit_",
			"jtf_ResolverStyle__clinit_",
			"jtt_TemporalQueries__clinit_",
			"ju_GregorianCalendar__clinit_",
			"nlei_OpenGLObjects$VertexArrayGL__clinit_",
			"nlei_PlatformRuntime$8__clinit_",
			"nleo_EaglercraftGPU__clinit_",
			"nmac_ConstructBeaconTrigger__clinit_",
			"nmac_EnchantmentPredicate__clinit_",
			"nmac_EntityFlagsPredicate__clinit_",
			"nmac_ShotCrossbowTrigger__clinit_",
			"nmac_TickTrigger__clinit_",
			"nmb_DeadCoralPlantBlock__clinit_",
			"nmb_DispenserBlock__clinit_",
			"nmb_FourWayBlock$1__clinit_",
			"nmb_KelpTopBlock__clinit_",
			"nmb_LecternBlock__clinit_",
			"nmb_LeverBlock$1__clinit_",
			"nmb_PressurePlateBlock$1__clinit_",
			"nmb_RailBlock$1__clinit_",
			"nmb_ShulkerBoxBlock$1__clinit_",
			"nmb_SkullBlock__clinit_",
			"nmb_StemBlock__clinit_",
			"nmb_TripWireBlock__clinit_",
			"nmb_WallBannerBlock__clinit_",
			"nmca_EntityAnchorArgument__clinit_",
			"nmcgfp_UnicodeTextureGlyphProvider__clinit_",
			"nmcgr_RecipeBookGui__clinit_",
			"nmcgs_ChatOptionsScreen__clinit_",
			"nmcgs_ChatScreen__clinit_",
			"nmcgs_LoomScreen__clinit_",
			"nmci_DebugCommand__clinit_",
			"nmci_DeOpCommand__clinit_",
			"nmci_LocateCommand__clinit_",
			"nmcnp_NetworkPlayerInfo$1__clinit_",
			"nmcrc_ClippingHelperImpl__clinit_",
			"nmcrel_EnderDragonEyesLayer__clinit_",
			"nmcrc_VisGraph__clinit_",
			"nmcrm_ItemTransformVec3f$Deserializer__clinit_",
			"nmcu_InputMappings$Type__clinit_",
			"nmcu_InputMappings__clinit_",
			"nmeag_GoalSelector__clinit_",
			"nmebdp_HoldingPatternPhase__clinit_",
			"nmebdp_LandingApproachPhase__clinit_",
			"nmep_FishingBobberEntity$State__clinit_",
			"nmep_ProjectileItemEntity__clinit_",
			"nmi_MusicDiscItem__clinit_",
			"nmic_ClickType__clinit_",
			"nmn_NetworkManager__clinit_",
			"nmnps_SCombatPacket$1__clinit_",
			"nmnr_RConThread__clinit_",
			"nmp_Effects__clinit_",
			"nmrd_PackMetadataSection__clinit_",
			"nms_IStatFormatter__clinit_",
			"nmu_EntityPredicates__clinit_",
			"nmu_SharedConstants__clinit_",
			"nmuc_ThreadTaskExecutor__clinit_",
			"nmudf_EntityRenameing1510__clinit_",
			"nmum_RayTraceContext$BlockMode__clinit_",
			"nmur_Bootstrap__clinit_",
			"nmv_GossipType__clinit_",
			"nmwb_Biome$Category__clinit_",
			"nmwbp_BiomeProviderType__clinit_",
			"nmwgfs_OceanRuinStructure$Type__clinit_",
			"nmwgfs_ShipwreckPieces__clinit_",
			"nmwgfs_TemplateStructurePiece__clinit_",
			"nmwgl_AddIslandLayer__clinit_",
			"nmws_ChunkHolder$IChunkLoadingError__clinit_",
			"nmws_TicketType__clinit_",
			"nmwslf_LootFunctionManager__clinit_",
			"npem_ResourceLoader__clinit_",
			"oacif_PathUtils__clinit_",
			"oacif_TrueFileFilter__clinit_"
		};

		@Override
		public void begin(RenderingManager manager, BuildTarget buildTarget) {
			SourceWriter writer = manager.getWriter();
			writer.append("    // TeaVM can emit references to these JDK classes even when they are absent (EAGLER_SHIM_MARKER_v2)").newLine();
			writer.append("    // from the browser classlib. Provide inert stand-ins to avoid load-time (BROWSER_SHIM_SENTINEL)").newLine();
			writer.append("    // ReferenceError crashes before `main` is exported.").newLine();
			for(int i = 0; i < SHIMS.length; ++i) {
				writer.append("    if (typeof ").append(SHIMS[i]).append(" === 'undefined') {").newLine();
				writer.append("        var ").append(SHIMS[i]).append(" = $rt_classWithoutFields(0);").newLine();
				writer.append("    }").newLine();
			}
			writer.append("    // Some TeaVM classlib builds omit NoClassDefFoundError(String).").newLine();
			writer.append("    // Generated stubs still call jl_NoClassDefFoundError__init_(msg),").newLine();
			writer.append("    // which otherwise returns undefined and then crashes $rt_exception.").newLine();
			writer.append("    if (typeof jl_NoClassDefFoundError__init_ !== 'function') {").newLine();
			writer.append("        var jl_NoClassDefFoundError__init_ = function($message) {").newLine();
			writer.append("            var $obj = new jl_NoClassDefFoundError();").newLine();
			writer.append("            if (typeof jl_NoClassDefFoundError__init_2 === 'function') {").newLine();
			writer.append("                jl_NoClassDefFoundError__init_2($obj, $message);").newLine();
			writer.append("            } else if (typeof jl_LinkageError__init_2 === 'function') {").newLine();
			writer.append("                jl_LinkageError__init_2($obj, $message);").newLine();
			writer.append("            } else if (typeof jl_NoClassDefFoundError__init_1 === 'function') {").newLine();
			writer.append("                jl_NoClassDefFoundError__init_1($obj);").newLine();
			writer.append("            } else if (typeof jl_LinkageError__init_0 === 'function') {").newLine();
			writer.append("                jl_LinkageError__init_0($obj);").newLine();
			writer.append("            }").newLine();
			writer.append("            return $obj;").newLine();
			writer.append("        };").newLine();
			writer.append("    }").newLine();
			writer.append("    if (typeof jl_NoClassDefFoundError__init_2 !== 'function') {").newLine();
			writer.append("        var jl_NoClassDefFoundError__init_2 = function($this, $message) {").newLine();
			writer.append("            if (typeof jl_LinkageError__init_2 === 'function') {").newLine();
			writer.append("                jl_LinkageError__init_2($this, $message);").newLine();
			writer.append("            } else if (typeof jl_NoClassDefFoundError__init_1 === 'function') {").newLine();
			writer.append("                jl_NoClassDefFoundError__init_1($this);").newLine();
			writer.append("            } else if (typeof jl_LinkageError__init_0 === 'function') {").newLine();
			writer.append("                jl_LinkageError__init_0($this);").newLine();
			writer.append("            }").newLine();
			writer.append("        };").newLine();
			writer.append("    }").newLine();
			writer.append("    // Safety net: never let $rt_exception crash on undefined throwables.").newLine();
			writer.append("    var eagler$origRtException = $rt_exception;").newLine();
			writer.append("    $rt_exception = function(ex) {").newLine();
			writer.append("        if (ex === null || ex === $rt_globals.undefined) {").newLine();
			writer.append("            return new $rt_globals.TypeError(").newLine();
			writer.append("                    'TeaVM runtime attempted to throw an undefined Java exception object');").newLine();
			writer.append("        }").newLine();
			writer.append("        return eagler$origRtException(ex);").newLine();
			writer.append("    };").newLine();
			writer.append("    var eagler$shouldSwallowNoClassDef = function(javaEx) {").newLine();
			writer.append("        if (javaEx === null || javaEx === $rt_globals.undefined) {").newLine();
			writer.append("            return false;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (!$rt_isInstance(javaEx, jl_NoClassDefFoundError)) {").newLine();
				writer.append("            return false;").newLine();
			writer.append("        }").newLine();
			writer.append("        var msgObj = jl_Throwable_getMessage(javaEx);").newLine();
			writer.append("        if (msgObj === null || msgObj === $rt_globals.undefined) {").newLine();
			writer.append("            return true;").newLine();
			writer.append("        }").newLine();
			writer.append("        var msg = $rt_ustr(msgObj);").newLine();
			writer.append("        return msg.indexOf('Class not found: ') === 0").newLine();
			writer.append("                || msg.indexOf('Method not found: ') === 0;").newLine();
			writer.append("    };").newLine();
			writer.append("    // IMPORTANT: don't swallow from $rt_throw. TeaVM state-machine methods").newLine();
			writer.append("    // rely on throw-as-control-flow, and returning here corrupts pointers").newLine();
			writer.append("    // (manifesting as \"Invalid recorded state\").").newLine();
			writer.append("    // Gson TypeAdapters can end with an unconditional NoClassDefFoundError").newLine();
			writer.append("    // for AtomicIntegerArray in TeaVM browser classlibs. Recover by finishing").newLine();
			writer.append("    // factory setup and only swallow that specific late-clinit condition.").newLine();
			writer.append("    var eagler$origTypeAdaptersClinit = cggib_TypeAdapters__clinit_;").newLine();
			writer.append("    cggib_TypeAdapters__clinit_ = function() {").newLine();
			writer.append("        try {").newLine();
			writer.append("            eagler$origTypeAdaptersClinit();").newLine();
			writer.append("        } catch (e) {").newLine();
			writer.append("            if (cggib_TypeAdapters_ATOMIC_INTEGER_ARRAY !== null").newLine();
			writer.append("                    && cggib_TypeAdapters_ATOMIC_INTEGER_ARRAY_FACTORY === null) {").newLine();
			writer.append("                cggib_TypeAdapters_ATOMIC_INTEGER_ARRAY_FACTORY = cggib_TypeAdapters_newFactory0(").newLine();
			writer.append("                        $rt_cls(juca_AtomicIntegerArray), cggib_TypeAdapters_ATOMIC_INTEGER_ARRAY);").newLine();
			writer.append("                return;").newLine();
			writer.append("            }").newLine();
			writer.append("            throw e;").newLine();
			writer.append("        }").newLine();
			writer.append("    };").newLine();
			writer.append("    // Patch Gson Excluder anonymous/local check to avoid runtime calls").newLine();
			writer.append("    // to Class.isAnonymousClass() on TeaVM classlib variants that omit it.").newLine();
			writer.append("    if (typeof cggi_Excluder_isAnonymousOrLocal === 'function'").newLine();
			writer.append("            && !cggi_Excluder_isAnonymousOrLocal.$eaglerPatchedAnonClass) {").newLine();
			writer.append("        cggi_Excluder_isAnonymousOrLocal = function($this, $clazz) {").newLine();
			writer.append("            if ($clazz === null || $clazz === $rt_globals.undefined) {").newLine();
			writer.append("                return 0;").newLine();
			writer.append("            }").newLine();
			writer.append("            try {").newLine();
			writer.append("                if (typeof jl_Enum !== 'undefined'").newLine();
			writer.append("                        && $rt_cls(jl_Enum).$isAssignableFrom($clazz)) {").newLine();
			writer.append("                    return 0;").newLine();
			writer.append("                }").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("            try {").newLine();
			writer.append("                if (typeof $clazz.$isLocalClass === 'function'").newLine();
			writer.append("                        && $clazz.$isLocalClass()) {").newLine();
			writer.append("                    return 1;").newLine();
			writer.append("                }").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("            try {").newLine();
			writer.append("                if (typeof $clazz.$getName0 === 'function') {").newLine();
			writer.append("                    var nObj = $clazz.$getName0();").newLine();
			writer.append("                    if (nObj !== null && nObj !== $rt_globals.undefined) {").newLine();
			writer.append("                        var n = $rt_ustr(nObj);").newLine();
			writer.append("                        var idx = n.lastIndexOf('$');").newLine();
			writer.append("                        if (idx >= 0 && idx + 1 < n.length) {").newLine();
			writer.append("                            var anon = true;").newLine();
			writer.append("                            for (var i = idx + 1; i < n.length; ++i) {").newLine();
			writer.append("                                var ch = n.charCodeAt(i);").newLine();
			writer.append("                                if (ch < 48 || ch > 57) {").newLine();
			writer.append("                                    anon = false;").newLine();
			writer.append("                                    break;").newLine();
			writer.append("                                }").newLine();
			writer.append("                            }").newLine();
			writer.append("                            if (anon) {").newLine();
			writer.append("                                return 1;").newLine();
			writer.append("                            }").newLine();
			writer.append("                        }").newLine();
			writer.append("                    }").newLine();
			writer.append("                }").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("            return 0;").newLine();
			writer.append("        };").newLine();
			writer.append("        cggi_Excluder_isAnonymousOrLocal.$eaglerPatchedAnonClass = true;").newLine();
			writer.append("    }").newLine();
			writer.append("    // Some TeaVM builds still emit hard NoClassDefFoundError stubs for").newLine();
			writer.append("    // AtomicReferenceArray-dependent paths. Patch the affected call-sites").newLine();
			writer.append("    // after script evaluation so startup can proceed.").newLine();
			writer.append("    var eagler$patchAtomicReferenceArrayStubs = function() {").newLine();
			writer.append("        if (typeof cgcc_LocalCache$LocalLoadingCache__init_ === 'function'").newLine();
			writer.append("                && !cgcc_LocalCache$LocalLoadingCache__init_.$eaglerPatched) {").newLine();
			writer.append("            cgcc_LocalCache$LocalLoadingCache__init_ = function($this, $builder, $loader) {").newLine();
			writer.append("                var eagler$cache = {").newLine();
			writer.append("                    $loader : $loader,").newLine();
			writer.append("                    $map : new $rt_globals.Map()").newLine();
			writer.append("                };").newLine();
			writer.append("                eagler$cache.$getOrLoad = function($key) {").newLine();
			writer.append("                    var $ptr, $tmp;").newLine();
			writer.append("                    $ptr = 0;").newLine();
			writer.append("                    if ($rt_resuming()) {").newLine();
			writer.append("                        var $thread = $rt_nativeThread();").newLine();
			writer.append("                        $ptr = $thread.pop();").newLine();
			writer.append("                        $key = $thread.pop();").newLine();
			writer.append("                    }").newLine();
			writer.append("                    main: while (true) { switch ($ptr) {").newLine();
			writer.append("                    case 0:").newLine();
			writer.append("                        if (this.$map.has($key)) {").newLine();
			writer.append("                            return this.$map.get($key);").newLine();
			writer.append("                        }").newLine();
			writer.append("                        $ptr = 1;").newLine();
			writer.append("                    case 1:").newLine();
			writer.append("                        $tmp = this.$loader.$load($key);").newLine();
			writer.append("                        if ($rt_suspending()) {").newLine();
			writer.append("                            break main;").newLine();
			writer.append("                        }").newLine();
			writer.append("                        this.$map.set($key, $tmp);").newLine();
			writer.append("                        return $tmp;").newLine();
			writer.append("                    default:").newLine();
			writer.append("                        $rt_invalidPointer();").newLine();
			writer.append("                    }}").newLine();
			writer.append("                    $rt_nativeThread().push($key, $ptr);").newLine();
			writer.append("                };").newLine();
			writer.append("                eagler$cache.$getIfPresent = function($key) {").newLine();
			writer.append("                    return this.$map.has($key) ? this.$map.get($key) : null;").newLine();
			writer.append("                };").newLine();
			writer.append("                eagler$cache.$put = function($key, $value) {").newLine();
			writer.append("                    this.$map.set($key, $value);").newLine();
			writer.append("                };").newLine();
			writer.append("                eagler$cache.$clear = function() {").newLine();
			writer.append("                    this.$map.clear();").newLine();
			writer.append("                };").newLine();
			writer.append("                eagler$cache.$size = function() {").newLine();
			writer.append("                    return this.$map.size | 0;").newLine();
			writer.append("                };").newLine();
			writer.append("                cgcc_LocalCache$LocalManualCache__init_2($this, eagler$cache, null);").newLine();
			writer.append("            };").newLine();
			writer.append("            cgcc_LocalCache$LocalLoadingCache__init_.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof cgcc_MapMaker_makeMap === 'function'").newLine();
			writer.append("                && !cgcc_MapMaker_makeMap.$eaglerPatched) {").newLine();
			writer.append("            cgcc_MapMaker_makeMap = function($this) {").newLine();
			writer.append("                return juc_ConcurrentHashMap__init_();").newLine();
			writer.append("            };").newLine();
			writer.append("            cgcc_MapMaker_makeMap.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof cmay_YggdrasilMinecraftSessionService__init_ === 'function'").newLine();
			writer.append("                && !cmay_YggdrasilMinecraftSessionService__init_.$eaglerPatchedNoPublicKey) {").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService__init_ = function($this, $authenticationService) {").newLine();
			writer.append("                var var$2, var$3, var$4, $ptr, $tmp;").newLine();
			writer.append("                $ptr = 0;").newLine();
			writer.append("                if ($rt_resuming()) {").newLine();
			writer.append("                    var $thread = $rt_nativeThread();").newLine();
			writer.append("                    $ptr = $thread.pop();").newLine();
			writer.append("                    var$4 = $thread.pop();").newLine();
			writer.append("                    var$3 = $thread.pop();").newLine();
			writer.append("                    var$2 = $thread.pop();").newLine();
			writer.append("                    $authenticationService = $thread.pop();").newLine();
			writer.append("                    $this = $thread.pop();").newLine();
			writer.append("                }").newLine();
			writer.append("                main: while (true) { switch ($ptr) {").newLine();
			writer.append("                case 0:").newLine();
			writer.append("                    $ptr = 1;").newLine();
			writer.append("                case 1:").newLine();
			writer.append("                    cmay_YggdrasilMinecraftSessionService_$callClinit();").newLine();
			writer.append("                    if ($rt_suspending()) {").newLine();
			writer.append("                        break main;").newLine();
			writer.append("                    }").newLine();
			writer.append("                    cmam_HttpMinecraftSessionService__init_($this, $authenticationService);").newLine();
			writer.append("                    var$2 = cgg_GsonBuilder__init_();").newLine();
			writer.append("                    var$3 = $rt_cls(ju_UUID);").newLine();
			writer.append("                    var$4 = cmu_UUIDTypeAdapter__init_();").newLine();
			writer.append("                    $ptr = 2;").newLine();
			writer.append("                case 2:").newLine();
			writer.append("                    $tmp = cgg_GsonBuilder_registerTypeAdapter(var$2, var$3, var$4);").newLine();
			writer.append("                    if ($rt_suspending()) {").newLine();
			writer.append("                        break main;").newLine();
			writer.append("                    }").newLine();
			writer.append("                    var$4 = $tmp;").newLine();
			writer.append("                    $ptr = 3;").newLine();
			writer.append("                case 3:").newLine();
			writer.append("                    $tmp = cgg_GsonBuilder_create(var$4);").newLine();
			writer.append("                    if ($rt_suspending()) {").newLine();
			writer.append("                        break main;").newLine();
			writer.append("                    }").newLine();
			writer.append("                    var$4 = $tmp;").newLine();
			writer.append("                    $this.$gson3 = var$4;").newLine();
			writer.append("                    var$4 = cgcc_CacheBuilder_newBuilder();").newLine();
			writer.append("                    juc_TimeUnit_$callClinit();").newLine();
			writer.append("                    var$2 = juc_TimeUnit_HOURS;").newLine();
			writer.append("                    var$4 = cgcc_CacheBuilder_expireAfterWrite(var$4, Long_fromInt(6), var$2);").newLine();
			writer.append("                    var$2 = cmay_YggdrasilMinecraftSessionService$1__init_($this);").newLine();
			writer.append("                    $this.$insecureProfiles = cgcc_CacheBuilder_build(var$4, var$2);").newLine();
			writer.append("                    $this.$publicKey = null;").newLine();
			writer.append("                    return;").newLine();
			writer.append("                default:").newLine();
			writer.append("                    $rt_invalidPointer();").newLine();
			writer.append("                }}").newLine();
			writer.append("                $rt_nativeThread().push($this, $authenticationService, var$2, var$3, var$4, $ptr);").newLine();
			writer.append("            };").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService__init_.$eaglerPatchedNoPublicKey = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof oaci_IOUtils__clinit_ === 'function'").newLine();
			writer.append("                && !oaci_IOUtils__clinit_.$eaglerPatchedThreadLocalInit) {").newLine();
			writer.append("            oaci_IOUtils__clinit_ = function() {").newLine();
			writer.append("                ji_File_$callClinit();").newLine();
			writer.append("                oaci_IOUtils_DIR_SEPARATOR = ji_File_separatorChar;").newLine();
			writer.append("                oaci_IOUtils_EMPTY_BYTE_ARRAY = $rt_createByteArray(0);").newLine();
			writer.append("                oaci_IOUtils_LINE_SEPARATOR = jl_System_lineSeparator();").newLine();
			writer.append("                oaci_StandardLineSeparator_$callClinit();").newLine();
			writer.append("                oaci_IOUtils_LINE_SEPARATOR_UNIX =").newLine();
			writer.append("                        oaci_StandardLineSeparator_getString(oaci_StandardLineSeparator_LF);").newLine();
			writer.append("                oaci_IOUtils_LINE_SEPARATOR_WINDOWS =").newLine();
			writer.append("                        oaci_StandardLineSeparator_getString(oaci_StandardLineSeparator_CRLF);").newLine();
			writer.append("                oaci_IOUtils_SKIP_BYTE_BUFFER = {").newLine();
			writer.append("                    $get15 : function() {").newLine();
			writer.append("                        return oaci_IOUtils_byteArray0();").newLine();
			writer.append("                    }").newLine();
			writer.append("                };").newLine();
			writer.append("                oaci_IOUtils_SKIP_CHAR_BUFFER = {").newLine();
			writer.append("                    $get15 : function() {").newLine();
			writer.append("                        return oaci_IOUtils_charArray();").newLine();
			writer.append("                    }").newLine();
			writer.append("                };").newLine();
			writer.append("            };").newLine();
			writer.append("            oaci_IOUtils__clinit_.$eaglerPatchedThreadLocalInit = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof cgcc_CacheBuilder$SimpleManualCache__init_0 === 'function'").newLine();
			writer.append("                && typeof juc_ConcurrentHashMap__init_3 === 'function'").newLine();
			writer.append("                && !cgcc_CacheBuilder$SimpleManualCache__init_0.$eaglerPatchedCHM) {").newLine();
			writer.append("            cgcc_CacheBuilder$SimpleManualCache__init_0 = function($this, $initialCapacity, $concurrencyLevel, $maxSize, $expireAfterWriteNanos, $expireAfterAccessNanos) {").newLine();
			writer.append("                var $map, $key, $key2, $mapFieldSet, $sizeFieldSet;").newLine();
			writer.append("                jl_Object__init_0($this);").newLine();
			writer.append("                $initialCapacity = jl_Math_max(1, $initialCapacity);").newLine();
			writer.append("                jl_Math_max(1, $concurrencyLevel);").newLine();
			writer.append("                $map = juc_ConcurrentHashMap__init_3($initialCapacity, 0.75);").newLine();
			writer.append("                $mapFieldSet = false;").newLine();
			writer.append("                for ($key in $this) {").newLine();
			writer.append("                    if ($key.indexOf('$map') === 0) {").newLine();
			writer.append("                        $this[$key] = $map;").newLine();
			writer.append("                        $mapFieldSet = true;").newLine();
			writer.append("                        break;").newLine();
			writer.append("                    }").newLine();
			writer.append("                }").newLine();
			writer.append("                if (!$mapFieldSet) {").newLine();
			writer.append("                    $this.$map13 = $map;").newLine();
			writer.append("                }").newLine();
			writer.append("                $sizeFieldSet = false;").newLine();
			writer.append("                for ($key2 in $this) {").newLine();
			writer.append("                    if ($key2.indexOf('$maxSize') === 0) {").newLine();
			writer.append("                        $this[$key2] = $maxSize;").newLine();
			writer.append("                        $sizeFieldSet = true;").newLine();
			writer.append("                        break;").newLine();
			writer.append("                    }").newLine();
			writer.append("                }").newLine();
			writer.append("                if (!$sizeFieldSet) {").newLine();
			writer.append("                    $this.$maxSize = $maxSize;").newLine();
			writer.append("                }").newLine();
			writer.append("            };").newLine();
			writer.append("            cgcc_CacheBuilder$SimpleManualCache__init_0.$eaglerPatchedCHM = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmcm_ClientChunkProvider$ChunkArray_get === 'function'").newLine();
			writer.append("                && !nmcm_ClientChunkProvider$ChunkArray_get.$eaglerPatched) {").newLine();
			writer.append("            nmcm_ClientChunkProvider$ChunkArray_get = function($this, $idx) {").newLine();
			writer.append("                if ($this.$chunks1 === null || $this.$chunks1 === $rt_globals.undefined) {").newLine();
			writer.append("                    $this.$chunks1 = [];").newLine();
			writer.append("                }").newLine();
			writer.append("                return $this.$chunks1[$idx] === $rt_globals.undefined ? null : $this.$chunks1[$idx];").newLine();
			writer.append("            };").newLine();
			writer.append("            nmcm_ClientChunkProvider$ChunkArray_get.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmcm_ClientChunkProvider_makeString === 'function'").newLine();
			writer.append("                && !nmcm_ClientChunkProvider_makeString.$eaglerPatched) {").newLine();
			writer.append("            nmcm_ClientChunkProvider_makeString = function($this) {").newLine();
			writer.append("                return $rt_str('Client Chunk Cache');").newLine();
			writer.append("            };").newLine();
			writer.append("            nmcm_ClientChunkProvider_makeString.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmws_ChunkHolder__init_0 === 'function'").newLine();
			writer.append("                && !nmws_ChunkHolder__init_0.$eaglerPatched) {").newLine();
			writer.append("            nmws_ChunkHolder__init_0 = function($this, $pos, $level, $lightManager, $listener, $provider) {").newLine();
			writer.append("                nmws_ChunkHolder_$callClinit();").newLine();
			writer.append("                jl_Object__init_0($this);").newLine();
			writer.append("                $this.$field_219312_g = [];").newLine();
			writer.append("                $this.$field_222983_h = nmws_ChunkHolder_UNLOADED_CHUNK_FUTURE;").newLine();
			writer.append("                $this.$field_219313_h = nmws_ChunkHolder_UNLOADED_CHUNK_FUTURE;").newLine();
			writer.append("                $this.$field_219314_i = nmws_ChunkHolder_UNLOADED_CHUNK_FUTURE;").newLine();
			writer.append("                $this.$field_219315_j = nmu_CF_completedFuture(null);").newLine();
			writer.append("                $this.$field_219316_k = nmws_ChunkManager_MAX_LOADED_LEVEL + 1 | 0;").newLine();
			writer.append("                $this.$field_219317_l = $this.$field_219316_k;").newLine();
			writer.append("                $this.$field_219318_m = $this.$field_219316_k;").newLine();
			writer.append("                $this.$pos0 = $pos;").newLine();
			writer.append("                $this.$lightManager = $lightManager;").newLine();
			writer.append("                $this.$field_219327_v = $listener;").newLine();
			writer.append("                $this.$playerProvider = $provider;").newLine();
			writer.append("                $this.$changedBlockPositions = $rt_createShortArray(64);").newLine();
			writer.append("                $this.$field_219329_x = 0;").newLine();
			writer.append("                $this.$func_219292_a($level);").newLine();
			writer.append("            };").newLine();
			writer.append("            nmws_ChunkHolder__init_0.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmws_ChunkHolder_func_219301_a === 'function'").newLine();
			writer.append("                && !nmws_ChunkHolder_func_219301_a.$eaglerPatched) {").newLine();
			writer.append("            nmws_ChunkHolder_func_219301_a = function($this, $status) {").newLine();
			writer.append("                var $idx = $status.$ordinal0();").newLine();
			writer.append("                var $value = $this.$field_219312_g[$idx];").newLine();
			writer.append("                return $value === $rt_globals.undefined || $value === null ?").newLine();
			writer.append("                        nmws_ChunkHolder_MISSING_CHUNK_FUTURE : $value;").newLine();
			writer.append("            };").newLine();
			writer.append("            nmws_ChunkHolder_func_219301_a.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmws_ChunkHolder_func_219276_a === 'function'").newLine();
			writer.append("                && !nmws_ChunkHolder_func_219276_a.$eaglerPatched) {").newLine();
			writer.append("            nmws_ChunkHolder_func_219276_a = function($this, $status, $chunkManager) {").newLine();
			writer.append("                return nmws_ChunkHolder_MISSING_CHUNK_FUTURE;").newLine();
			writer.append("            };").newLine();
			writer.append("            nmws_ChunkHolder_func_219276_a.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmws_ChunkHolder_func_219291_a === 'function'").newLine();
			writer.append("                && !nmws_ChunkHolder_func_219291_a.$eaglerPatched) {").newLine();
			writer.append("            nmws_ChunkHolder_func_219291_a = function($this, $chunkManager) {};").newLine();
			writer.append("            nmws_ChunkHolder_func_219291_a.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof nmws_ChunkHolder_func_219294_a === 'function'").newLine();
			writer.append("                && !nmws_ChunkHolder_func_219294_a.$eaglerPatched) {").newLine();
			writer.append("            nmws_ChunkHolder_func_219294_a = function($this, $wrapper) {};").newLine();
			writer.append("            nmws_ChunkHolder_func_219294_a.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        // Browser runtime intentionally lacks full java.security").newLine();
			writer.append("        // implementation; force authlib skin/profile fetches to").newLine();
			writer.append("        // non-secure mode to avoid signature-class hard failures.").newLine();
			writer.append("        if (typeof cmay_YggdrasilMinecraftSessionService_getTextures === 'function'").newLine();
			writer.append("                && !cmay_YggdrasilMinecraftSessionService_getTextures.$eaglerPatchedNonSecure) {").newLine();
			writer.append("            var eagler$origGetTextures = cmay_YggdrasilMinecraftSessionService_getTextures;").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService_getTextures = function($this, $profile, $requireSecure) {").newLine();
			writer.append("                return eagler$origGetTextures($this, $profile, 0);").newLine();
			writer.append("            };").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService_getTextures.$eaglerPatchedNonSecure = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof cmay_YggdrasilMinecraftSessionService_fillProfileProperties === 'function'").newLine();
			writer.append("                && !cmay_YggdrasilMinecraftSessionService_fillProfileProperties.$eaglerPatchedNonSecure) {").newLine();
			writer.append("            var eagler$origFillProfile = cmay_YggdrasilMinecraftSessionService_fillProfileProperties;").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService_fillProfileProperties = function($this, $profile, $requireSecure) {").newLine();
			writer.append("                return eagler$origFillProfile($this, $profile, 0);").newLine();
			writer.append("            };").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService_fillProfileProperties.$eaglerPatchedNonSecure = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof cmay_YggdrasilMinecraftSessionService_fillGameProfile === 'function'").newLine();
			writer.append("                && !cmay_YggdrasilMinecraftSessionService_fillGameProfile.$eaglerPatchedNonSecure) {").newLine();
			writer.append("            var eagler$origFillGame = cmay_YggdrasilMinecraftSessionService_fillGameProfile;").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService_fillGameProfile = function($this, $profile, $requireSecure) {").newLine();
			writer.append("                return eagler$origFillGame($this, $profile, 0);").newLine();
			writer.append("            };").newLine();
			writer.append("            cmay_YggdrasilMinecraftSessionService_fillGameProfile.$eaglerPatchedNonSecure = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        // Authlib expects Proxy to be non-null, but browser bootstrap").newLine();
			writer.append("        // may intentionally pass null. Keep constructor resilient.").newLine();
			writer.append("        if (typeof cma_HttpAuthenticationService__init_ === 'function'").newLine();
			writer.append("                && !cma_HttpAuthenticationService__init_.$eaglerPatched) {").newLine();
			writer.append("            cma_HttpAuthenticationService__init_ = function($this, $proxy) {").newLine();
			writer.append("                var $ptr;").newLine();
			writer.append("                $ptr = 0;").newLine();
			writer.append("                if ($rt_resuming()) {").newLine();
			writer.append("                    var $thread = $rt_nativeThread();").newLine();
			writer.append("                    $ptr = $thread.pop();").newLine();
			writer.append("                    $proxy = $thread.pop();").newLine();
			writer.append("                    $this = $thread.pop();").newLine();
			writer.append("                }").newLine();
			writer.append("                main: while (true) { switch ($ptr) {").newLine();
			writer.append("                case 0:").newLine();
			writer.append("                    $ptr = 1;").newLine();
			writer.append("                case 1:").newLine();
			writer.append("                    cma_HttpAuthenticationService_$callClinit();").newLine();
			writer.append("                    if ($rt_suspending()) {").newLine();
			writer.append("                        break main;").newLine();
			writer.append("                    }").newLine();
			writer.append("                    cma_BaseAuthenticationService__init_($this);").newLine();
			writer.append("                    $this.$proxy1 = $proxy;").newLine();
			writer.append("                    return;").newLine();
			writer.append("                default:").newLine();
			writer.append("                    $rt_invalidPointer();").newLine();
			writer.append("                }}").newLine();
			writer.append("                $rt_nativeThread().push($this, $proxy, $ptr);").newLine();
			writer.append("            };").newLine();
			writer.append("            cma_HttpAuthenticationService__init_.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        // Keep authlib Gson bootstrap resilient when forks strip").newLine();
			writer.append("        // adapter interfaces from optional classes.").newLine();
			writer.append("        if (typeof cgg_GsonBuilder_registerTypeAdapter === 'function'").newLine();
			writer.append("                && !cgg_GsonBuilder_registerTypeAdapter.$eaglerPatchedUUID) {").newLine();
			writer.append("            var eagler$origRegisterTypeAdapter = cgg_GsonBuilder_registerTypeAdapter;").newLine();
			writer.append("            cgg_GsonBuilder_registerTypeAdapter = function($this, $type, $typeAdapter) {").newLine();
			writer.append("                if (typeof cmu_UUIDTypeAdapter === 'function'").newLine();
			writer.append("                        && $typeAdapter instanceof cmu_UUIDTypeAdapter) {").newLine();
			writer.append("                    return $this;").newLine();
			writer.append("                }").newLine();
			writer.append("                var eagler$authlibAdapter =").newLine();
			writer.append("                        (typeof cmay_YggdrasilAuthenticationService$GameProfileSerializer === 'function'").newLine();
			writer.append("                                && $typeAdapter instanceof cmay_YggdrasilAuthenticationService$GameProfileSerializer)").newLine();
			writer.append("                        || (typeof cmap_PropertyMap$Serializer === 'function'").newLine();
			writer.append("                                && $typeAdapter instanceof cmap_PropertyMap$Serializer)").newLine();
			writer.append("                        || (typeof cmayr_ProfileSearchResultsResponse$Serializer === 'function'").newLine();
			writer.append("                                && $typeAdapter instanceof cmayr_ProfileSearchResultsResponse$Serializer);").newLine();
			writer.append("                try {").newLine();
			writer.append("                    return eagler$origRegisterTypeAdapter($this, $type, $typeAdapter);").newLine();
			writer.append("                } catch (e) {").newLine();
			writer.append("                    var javaEx = (e === null || e === $rt_globals.undefined)").newLine();
			writer.append("                            ? null : e[$rt_javaExceptionProp];").newLine();
			writer.append("                    if (eagler$authlibAdapter && javaEx !== null").newLine();
			writer.append("                            && javaEx !== $rt_globals.undefined").newLine();
			writer.append("                            && $rt_isInstance(javaEx, jl_IllegalArgumentException)) {").newLine();
			writer.append("                        return $this;").newLine();
			writer.append("                    }").newLine();
			writer.append("                    throw e;").newLine();
			writer.append("                }").newLine();
			writer.append("            };").newLine();
			writer.append("            cgg_GsonBuilder_registerTypeAdapter.$eaglerPatchedUUID = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        // TeaVM may stub TypeToken generic-super lookup with a hard throw.").newLine();
			writer.append("        // Provide a browser-safe fallback for known call-sites.").newLine();
			writer.append("        if (typeof cggr_TypeToken_getSuperclassTypeParameter === 'function'").newLine();
			writer.append("                && !cggr_TypeToken_getSuperclassTypeParameter.$eaglerPatched) {").newLine();
			writer.append("            cggr_TypeToken_getSuperclassTypeParameter = function($subclass) {").newLine();
			writer.append("                var $nameObj = null;").newLine();
			writer.append("                var $name = null;").newLine();
			writer.append("                if (typeof jl_Class_getName === 'function') {").newLine();
			writer.append("                    $nameObj = jl_Class_getName($subclass);").newLine();
			writer.append("                    if ($nameObj !== null && $nameObj !== $rt_globals.undefined) {").newLine();
			writer.append("                        $name = $rt_ustr($nameObj);").newLine();
			writer.append("                    }").newLine();
			writer.append("                }").newLine();
			writer.append("                if ($name === 'net.minecraft.advancements.PlayerAdvancements$1'").newLine();
			writer.append("                        && typeof cggi_$Gson$Types_newParameterizedTypeWithOwner === 'function') {").newLine();
			writer.append("                    var $args = $rt_createArray(jlr_Type, 2);").newLine();
			writer.append("                    $args.data[0] = $rt_cls(nmu_ResourceLocation);").newLine();
			writer.append("                    $args.data[1] = $rt_cls(nma_AdvancementProgress);").newLine();
			writer.append("                    return cggi_$Gson$Types_newParameterizedTypeWithOwner(").newLine();
			writer.append("                            null, $rt_cls(ju_Map), $args);").newLine();
			writer.append("                }").newLine();
			writer.append("                return $rt_cls(jl_Object);").newLine();
			writer.append("            };").newLine();
			writer.append("            cggr_TypeToken_getSuperclassTypeParameter.$eaglerPatched = true;").newLine();
			writer.append("        }").newLine();
			writer.append("    };").newLine();
			writer.append("    // Guava AbstractFuture can contain hard NoClassDefFoundError stubs").newLine();
			writer.append("    // for LockSupport when browser classlib omits it. Patch those paths").newLine();
			writer.append("    // so startup logic degrades safely instead of crashing the client.").newLine();
			writer.append("    var eagler$patchLockSupportStubs = function() {").newLine();
			writer.append("        if (typeof cgcuc_AbstractFuture$Waiter_unpark === 'function'").newLine();
			writer.append("                && !cgcuc_AbstractFuture$Waiter_unpark.$eaglerPatchedLockSupport) {").newLine();
			writer.append("            cgcuc_AbstractFuture$Waiter_unpark = function($this) {").newLine();
			writer.append("                var $w = $this.$thread;").newLine();
			writer.append("                if ($w === null || $w === $rt_globals.undefined) {").newLine();
			writer.append("                    return;").newLine();
			writer.append("                }").newLine();
			writer.append("                $this.$thread = null;").newLine();
			writer.append("                if (typeof jl_Thread_interrupt === 'function') {").newLine();
			writer.append("                    jl_Thread_interrupt($w);").newLine();
			writer.append("                }").newLine();
			writer.append("            };").newLine();
			writer.append("            cgcuc_AbstractFuture$Waiter_unpark.$eaglerPatchedLockSupport = true;").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof cgcuc_AbstractFuture_get === 'function'").newLine();
			writer.append("                && !cgcuc_AbstractFuture_get.$eaglerPatchedLockSupport) {").newLine();
			writer.append("            var eagler$origAbstractFutureGet = cgcuc_AbstractFuture_get;").newLine();
			writer.append("            cgcuc_AbstractFuture_get = function($this) {").newLine();
			writer.append("                try {").newLine();
			writer.append("                    return eagler$origAbstractFutureGet($this);").newLine();
			writer.append("                } catch (e) {").newLine();
			writer.append("                    var javaEx = (e === null || e === $rt_globals.undefined)").newLine();
			writer.append("                            ? null : e[$rt_javaExceptionProp];").newLine();
			writer.append("                    if (!eagler$shouldSwallowNoClassDef(javaEx)) {").newLine();
			writer.append("                        throw e;").newLine();
			writer.append("                    }").newLine();
			writer.append("                    var $value = $this.$value13;").newLine();
			writer.append("                    if (($value !== null ? 1 : 0)").newLine();
			writer.append("                            && !($value instanceof cgcuc_AbstractFuture$SetFuture)) {").newLine();
			writer.append("                        return cgcuc_AbstractFuture_getDoneValue($this, $value);").newLine();
			writer.append("                    }").newLine();
			writer.append("                    return null;").newLine();
			writer.append("                }").newLine();
			writer.append("            };").newLine();
			writer.append("            cgcuc_AbstractFuture_get.$eaglerPatchedLockSupport = true;").newLine();
			writer.append("        }").newLine();
			writer.append("    };").newLine();
			writer.append("    // Wrap generated __clinit_ stubs that only exist because TeaVM").newLine();
			writer.append("    // couldn't resolve optional desktop/server classes. These should").newLine();
			writer.append("    // never be fatal in the browser runtime.").newLine();
			writer.append("    var eagler$patchNoClassDefClinitStubs = function() {").newLine();
			writer.append("        var eagler$shouldSwallow = function(e) {").newLine();
			writer.append("            if (e === null || e === $rt_globals.undefined) {").newLine();
			writer.append("                return false;").newLine();
			writer.append("            }").newLine();
			writer.append("            var javaEx = e[$rt_javaExceptionProp];").newLine();
			writer.append("            if (javaEx === null || javaEx === $rt_globals.undefined) {").newLine();
			writer.append("                return false;").newLine();
			writer.append("            }").newLine();
			writer.append("            if (!$rt_isInstance(javaEx, jl_NoClassDefFoundError)) {").newLine();
			writer.append("                return false;").newLine();
			writer.append("            }").newLine();
			writer.append("            var msgObj = jl_Throwable_getMessage(javaEx);").newLine();
			writer.append("            if (msgObj === null || msgObj === $rt_globals.undefined) {").newLine();
			writer.append("                return true;").newLine();
			writer.append("            }").newLine();
			writer.append("            var msg = $rt_ustr(msgObj);").newLine();
			writer.append("            return msg.indexOf('Class not found: ') === 0").newLine();
			writer.append("                    || msg.indexOf('Method not found: ') === 0;").newLine();
			writer.append("        };").newLine();
			writer.append("        var eagler$patchOne = function(name) {").newLine();
			writer.append("            try {").newLine();
			writer.append("                var fn = eval(name);").newLine();
			writer.append("                if (typeof fn !== 'function' || fn.$eaglerPatchedNoClassDef) {").newLine();
			writer.append("                    return;").newLine();
			writer.append("                }").newLine();
			writer.append("                var wrapped = function() {").newLine();
			writer.append("                    try {").newLine();
			writer.append("                        return fn.apply(this, arguments);").newLine();
			writer.append("                    } catch (e) {").newLine();
			writer.append("                        if (eagler$shouldSwallow(e)) {").newLine();
			writer.append("                            return;").newLine();
			writer.append("                        }").newLine();
			writer.append("                        throw e;").newLine();
			writer.append("                    }").newLine();
			writer.append("                };").newLine();
			writer.append("                wrapped.$eaglerPatchedNoClassDef = true;").newLine();
			writer.append("                eval(name + ' = wrapped;');").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("        };").newLine();
			writer.append("        var eagler$clinitStubs = [").newLine();
			for(int i = 0; i < NOCLASSDEF_CLINIT_STUBS.length; ++i) {
				writer.append("            '").append(NOCLASSDEF_CLINIT_STUBS[i]).append("',").newLine();
			}
			writer.append("        ];").newLine();
			writer.append("        for (var i = 0; i < eagler$clinitStubs.length; ++i) {").newLine();
			writer.append("            eagler$patchOne(eagler$clinitStubs[i]);").newLine();
			writer.append("        }").newLine();
			writer.append("    };").newLine();
			writer.append("    // Gson 2.8.0 may call Class.isAnonymousClass(), which some TeaVM").newLine();
			writer.append("    // classlib builds do not implement. Patch Excluder directly so").newLine();
			writer.append("    // startup does not terminate with NoSuchMethodError.").newLine();
			writer.append("    var eagler$patchGsonAnonymousClassStub = function() {").newLine();
			writer.append("        if (typeof cggi_Excluder_isAnonymousOrLocal === 'function'").newLine();
			writer.append("                && !cggi_Excluder_isAnonymousOrLocal.$eaglerPatchedAnonClass) {").newLine();
			writer.append("            cggi_Excluder_isAnonymousOrLocal = function($this, $clazz) {").newLine();
			writer.append("                if ($clazz === null || $clazz === $rt_globals.undefined) {").newLine();
			writer.append("                    return 0;").newLine();
			writer.append("                }").newLine();
			writer.append("                try {").newLine();
			writer.append("                    if (typeof jl_Enum !== 'undefined'").newLine();
			writer.append("                            && $rt_cls(jl_Enum).$isAssignableFrom($clazz)) {").newLine();
			writer.append("                        return 0;").newLine();
			writer.append("                    }").newLine();
			writer.append("                } catch (ignored) {}").newLine();
			writer.append("                try {").newLine();
			writer.append("                    if (typeof $clazz.$isLocalClass === 'function'").newLine();
			writer.append("                            && $clazz.$isLocalClass()) {").newLine();
			writer.append("                        return 1;").newLine();
			writer.append("                    }").newLine();
			writer.append("                } catch (ignored) {}").newLine();
			writer.append("                try {").newLine();
			writer.append("                    if (typeof $clazz.$getName0 === 'function') {").newLine();
			writer.append("                        var nObj = $clazz.$getName0();").newLine();
			writer.append("                        if (nObj !== null && nObj !== $rt_globals.undefined) {").newLine();
			writer.append("                            var n = $rt_ustr(nObj);").newLine();
			writer.append("                            var idx = n.lastIndexOf('$');").newLine();
			writer.append("                            if (idx >= 0 && idx + 1 < n.length) {").newLine();
			writer.append("                                var anon = true;").newLine();
			writer.append("                                for (var i = idx + 1; i < n.length; ++i) {").newLine();
			writer.append("                                    var ch = n.charCodeAt(i);").newLine();
			writer.append("                                    if (ch < 48 || ch > 57) {").newLine();
			writer.append("                                        anon = false;").newLine();
			writer.append("                                        break;").newLine();
			writer.append("                                    }").newLine();
			writer.append("                                }").newLine();
			writer.append("                                if (anon) {").newLine();
			writer.append("                                    return 1;").newLine();
			writer.append("                                }").newLine();
			writer.append("                            }").newLine();
			writer.append("                        }").newLine();
			writer.append("                    }").newLine();
			writer.append("                } catch (ignored) {}").newLine();
			writer.append("                return 0;").newLine();
			writer.append("            };").newLine();
			writer.append("            cggi_Excluder_isAnonymousOrLocal.$eaglerPatchedAnonClass = true;").newLine();
			writer.append("        }").newLine();
			writer.append("    };").newLine();
			writer.append("    var eagler$applyRuntimePatches = function() {").newLine();
			writer.append("        eagler$patchAtomicReferenceArrayStubs();").newLine();
			writer.append("        eagler$patchLockSupportStubs();").newLine();
			writer.append("        eagler$patchNoClassDefClinitStubs();").newLine();
			writer.append("        eagler$patchGsonAnonymousClassStub();").newLine();
			writer.append("    };").newLine();
			writer.append("    // Run now (for immediate startup) and again after eval (for late-defined stubs).").newLine();
			writer.append("    eagler$applyRuntimePatches();").newLine();
			writer.append("    if (typeof $rt_globals.queueMicrotask === 'function') {").newLine();
			writer.append("        $rt_globals.queueMicrotask(eagler$applyRuntimePatches);").newLine();
			writer.append("    } else if (typeof $rt_globals.Promise === 'function') {").newLine();
			writer.append("        $rt_globals.Promise.resolve().then(eagler$applyRuntimePatches);").newLine();
			writer.append("    }").newLine();
			writer.append("    if (typeof $rt_globals.setTimeout === 'function') {").newLine();
			writer.append("        $rt_globals.setTimeout(eagler$applyRuntimePatches, 0);").newLine();
			writer.append("    }").newLine();
		}

		@Override
		public void complete() {
		}

	}

}
